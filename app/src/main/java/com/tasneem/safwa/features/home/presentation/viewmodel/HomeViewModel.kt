package com.tasneem.safwa.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.home.domain.model.PromoBanner
import com.tasneem.safwa.features.brand.domain.usecase.GetBrandsUseCase
import com.tasneem.safwa.features.cart.domain.usecase.GetCartUseCase
import com.tasneem.safwa.features.home.domain.usecase.GetAiRecommendationsUseCase
import com.tasneem.safwa.features.home.domain.usecase.GetProductsUseCase
import com.tasneem.safwa.features.home.presentation.state.GreetingType
import com.tasneem.safwa.features.home.presentation.state.HomeEffect
import com.tasneem.safwa.features.home.presentation.state.HomeEvent
import com.tasneem.safwa.features.home.presentation.state.HomeState
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.settings.languageandcurrency.data.CurrencyRateManager
import com.tasneem.safwa.features.settings.languageandcurrency.data.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val cartRepository: CartRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getAiRecommendationsUseCase: GetAiRecommendationsUseCase,
    private val currencyRateManager: CurrencyRateManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    companion object {
        private const val HOME_BRANDS_COUNT = 8
    }

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                greeting = getGreeting(),
                promoBanners = getPromoBanners()
            )
        }
        loadCurrentUser()
        loadProducts()
        loadBrands()
        observeWishlist()
        loadCategories()
        observeCartCount()
        loadCartCount()
        loadAiRecommendations()


        viewModelScope.launch {
            currencyRateManager.displayCurrency
                .drop(1)
                .collect { loadProducts() }
        }

        viewModelScope.launch {
            languageManager.displayLanguage
                .drop(1)
                .collect { 
                    loadProducts() 
                    loadCategories()
                    loadBrands()
                    loadAiRecommendations()
                }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Resource.Success -> {
                    _state.update { it.copy(userName = result.data?.firstName ?: "Guest") }
                }
                else -> {
                    _state.update { it.copy(userName = "Guest") }
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val products = result.data
                        _state.update {
                            it.copy(
                                isLoading = false,
                                products = products,
                                filteredProducts = products,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadAiRecommendations() {
        viewModelScope.launch {
            getAiRecommendationsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isAiLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isAiLoading = false, aiRecommendations = result.data) }
                    }
                    is Resource.Error -> {
                        android.util.Log.e("SafwaAI", "AI Error: ${result.message}")
                        _state.update { it.copy(isAiLoading = false, aiErrorMessage = result.message) }
                    }
                }
            }
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            getBrandsUseCase().collect { result ->
                if (result is Resource.Success) {
                    _state.update { state ->
                        state.copy(brands = result.data.take(HOME_BRANDS_COUNT).map { it.name })
                    }
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                if (result is Resource.Success) {
                    _state.update { it.copy(categories = result.data, selectedCategory = null) }
                }
            }
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { result ->
                if (result is Resource.Success) {
                    _state.update { it.copy(favoriteProductIds = result.data.map { p -> p.id }.toSet()) }
                }
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.cartItemCount.collect { count ->
                _state.update { it.copy(cartItemCount = count) }
            }
        }
    }

    private fun loadCartCount() {
        viewModelScope.launch {
            getCartUseCase()
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadHome -> {
                loadProducts()
            }

            is HomeEvent.CategorySelected -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToCategoryProducts(event.category.handle))
                }
            }

            is HomeEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.product)
                }
            }

            is HomeEvent.ProductClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToProductDetails(event.product.handle))
                }
            }

            is HomeEvent.BrandClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToBrand(event.brand))
                }
            }

            is HomeEvent.CartClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToCart)
                }
            }

            is HomeEvent.ViewAllCategories -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToCategories)
                }
            }

            is HomeEvent.SeeAllBestSellers -> {
            }

            is HomeEvent.ViewAllBrands -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToBrands)
                }
            }

            is HomeEvent.ShopEditClicked -> {
            }
        }
    }

    private fun getGreeting(): GreetingType {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> GreetingType.MORNING
            hour < 18 -> GreetingType.AFTERNOON
            else -> GreetingType.EVENING
        }
    }

    private fun getPromoBanners(): List<PromoBanner> {
        return listOf(
            PromoBanner(
                id = "1",
                tagLabel = "BLACK FRIDAY",
                headline = "Massive savings!\nGet 80% off\neverything.",
                promoCode = "CODE_DISCOUNT_BLACKFRIDAY",
                imageUrl = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?q=80&w=2070",
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
                contentColor = androidx.compose.ui.graphics.Color.White
            ),
            PromoBanner(
                id = "2",
                tagLabel = "FREE SHIPPING",
                headline = "Buy 3 items\nor more and get\nfree shipping.",
                promoCode = "FREESHIPPING2026",
                imageUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?q=80&w=2070",
                backgroundColor = androidx.compose.ui.graphics.Color(0xFFE8C874),
                contentColor = androidx.compose.ui.graphics.Color(0xFF1A1A1A)
            ),
            PromoBanner(
                id = "3",
                tagLabel = "HALF PRICE",
                headline = "Upgrade your\ncollection at\nhalf the price.",
                promoCode = "BUY1GET50",
                imageUrl = "https://images.unsplash.com/photo-1492707892479-7bc8d5a4ee93?q=80&w=2000",
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF005D39),
                contentColor = androidx.compose.ui.graphics.Color.White
            ),
            PromoBanner(
                id = "4",
                tagLabel = "SUMMER BOGO",
                headline = "Buy one get\none free on\nsummer items.",
                promoCode = "CODE_BXGY_DISCOUNT_SUMMERBOGO",
                imageUrl = "https://images.unsplash.com/photo-1523362628745-0c100150b504?q=80&w=2036",
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF5D98B0),
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        )
    }
}


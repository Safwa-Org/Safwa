package com.tasneem.safwa.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.home.presentation.state.GreetingType
import com.tasneem.safwa.features.home.presentation.state.HomeEffect
import com.tasneem.safwa.features.home.presentation.state.HomeEvent
import com.tasneem.safwa.features.home.presentation.state.HomeState
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.wishlist.presentation.WishlistMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(HomeEvent.LoadHome)
        observeWishlist()
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                if (result is Resource.Success) {
                    val categoriesFromApi = result.data
                    val allCategory = Category(id = "all", title = "All", handle = "all", imageUrl = null)
                    val categories = listOf(allCategory) + categoriesFromApi
                    _state.update { it.copy(categories = categories, selectedCategory = allCategory) }
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

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadHome -> {
                _state.update { it.copy(isLoading = true) }
                val mockProducts = WishlistMockData.products
                val brands = mockProducts.map { it.vendor }.distinct().sorted()

                _state.update {
                    it.copy(
                        isLoading = false,
                        userName = "Ashraf",
                        greeting = getGreeting(),
                        products = mockProducts,
                        filteredProducts = mockProducts,
                        // categories updated via loadCategories()
                        brands = brands
                    )
                }
            }

            is HomeEvent.CategorySelected -> {
                viewModelScope.launch {
                    if (event.category.handle != "all") {
                        _effect.send(HomeEffect.NavigateToCategoryProducts(event.category.handle))
                    }
                }
            }

            is HomeEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.product)
                }
            }

            is HomeEvent.ProductClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToProductDetails(event.product.id))
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
}

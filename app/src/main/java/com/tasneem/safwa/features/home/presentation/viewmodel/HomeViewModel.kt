package com.tasneem.safwa.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.home.presentation.state.GreetingType
import com.tasneem.safwa.features.home.presentation.state.HomeEffect
import com.tasneem.safwa.features.home.presentation.state.HomeEvent
import com.tasneem.safwa.features.home.presentation.state.HomeState
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
    private val getWishlistUseCase: GetWishlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(HomeEvent.LoadHome)
        observeWishlist()
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
                val categories = WishlistMockData.defaultCategories +
                        mockProducts.map { it.productType }.distinct().sorted()
                val brands = mockProducts.map { it.vendor }.distinct().sorted()

                _state.update {
                    it.copy(
                        isLoading = false,
                        userName = "Ashraf",
                        greeting = getGreeting(),
                        products = mockProducts,
                        filteredProducts = mockProducts,
                        categories = categories,
                        brands = brands
                    )
                }
            }

            is HomeEvent.CategorySelected -> {
                _state.update { currentState ->
                    val filtered = if (event.category == "All") {
                        currentState.products
                    } else {
                        currentState.products.filter { it.productType == event.category }
                    }
                    currentState.copy(
                        selectedCategory = event.category,
                        filteredProducts = filtered
                    )
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

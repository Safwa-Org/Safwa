package com.tasneem.safwa.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.home.domain.usecase.GetProductsUseCase
import com.tasneem.safwa.features.home.presentation.state.GreetingType
import com.tasneem.safwa.features.home.presentation.state.HomeEffect
import com.tasneem.safwa.features.home.presentation.state.HomeEvent
import com.tasneem.safwa.features.home.presentation.state.HomeState
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
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
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update { it.copy(userName = "Ashraf", greeting = getGreeting()) }
        loadProducts()
        observeWishlist()
        loadCategories()
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
                        val brands = products.map { it.vendor }.distinct().sorted()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                products = products,
                                filteredProducts = products,
                                brands = brands,
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


package com.tasneem.safwa.features.brand.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.core.navigation.ScreenRoute
import com.tasneem.safwa.core.presentation.mapper.toUiError
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.brand.domain.usecase.GetBrandProductsUseCase
import com.tasneem.safwa.features.brand.presentation.state.BrandProductsEffect
import com.tasneem.safwa.features.brand.presentation.state.BrandProductsIntent
import com.tasneem.safwa.features.brand.presentation.state.BrandProductsUiState
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandProductsViewModel @Inject constructor(
    private val getBrandProductsUseCase: GetBrandProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val brandName: String = savedStateHandle.toRoute<ScreenRoute.BrandProducts>().brandName

    private val _uiState = MutableStateFlow(BrandProductsUiState())
    val uiState: StateFlow<BrandProductsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<BrandProductsEffect>()
    val uiEffect: SharedFlow<BrandProductsEffect> = _uiEffect.asSharedFlow()

    init {
        handleIntent(BrandProductsIntent.LoadProducts)
        observeWishlist()
    }

    fun handleIntent(intent: BrandProductsIntent) {
        when (intent) {
            is BrandProductsIntent.LoadProducts -> loadProducts()

            is BrandProductsIntent.OnProductClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(
                        BrandProductsEffect.NavigateToProductDetails(intent.product.handle)
                    )
                }
            }

            is BrandProductsIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.product)
                }
            }

            is BrandProductsIntent.OnBackClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(BrandProductsEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadProducts() {
        if (brandName.isBlank()) return
        viewModelScope.launch {
            getBrandProductsUseCase(brandName).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, products = result.data) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.throwable.toUiError())
                        }
                    }
                }
            }
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(favoriteProductIds = result.data.map { p -> p.id }.toSet())
                    }
                }
            }
        }
    }
}

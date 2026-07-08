package com.tasneem.safwa.features.category.presentation.category_products.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.presentation.mapper.toUiError
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.category.domain.usecase.GetProductsByCategoryUseCase
import com.tasneem.safwa.features.category.presentation.category_products.state.CategoryProductsState
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val categoryName: String = savedStateHandle.get<String>("categoryName") ?: ""

    private val _state = MutableStateFlow(CategoryProductsState())
    val state: StateFlow<CategoryProductsState> = _state.asStateFlow()

    init {
        loadProducts()
        observeWishlist()
    }

    fun retry() = loadProducts()

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { result ->
                if (result is Resource.Success) {
                    _state.update { it.copy(favoriteProductIds = result.data.map { p -> p.id }.toSet()) }
                }
            }
        }
    }

    private fun loadProducts() {
        if (categoryName.isBlank()) return
        viewModelScope.launch {
            getProductsByCategoryUseCase(categoryName).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, products = result.data, error = null) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.throwable.toUiError()) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
        }
    }
}


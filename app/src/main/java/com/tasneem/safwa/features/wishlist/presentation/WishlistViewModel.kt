package com.tasneem.safwa.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.wishlist.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.wishlist.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    init {
        onIntent(WishlistIntent.LoadWishlist)
        observeWishlist()
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val products = result.data
                        _state.update { currentState ->
                            val defaultCategories = listOf("All", "Fragrances", "Skincare")
                            val categories = (defaultCategories + products.map { it.productType }).distinct().sorted()
                            val selectedCategory = if (categories.contains(currentState.selectedCategory)) currentState.selectedCategory else "All"
                            val filteredProducts = if (selectedCategory == "All") {
                                products
                            } else {
                                products.filter { it.productType == selectedCategory }
                            }
                            
                            currentState.copy(
                                products = products,
                                filteredProducts = filteredProducts,
                                categories = categories,
                                selectedCategory = selectedCategory,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onIntent(intent: WishlistIntent) {
        when (intent) {
            is WishlistIntent.LoadWishlist -> {
                _state.update { it.copy(isLoading = true) }
            }
            is WishlistIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    val result = toggleFavoriteUseCase(intent.product)
                    if (result is Resource.Error) {
                        _state.update { it.copy(error = result.message) }
                    }
                }
            }
            is WishlistIntent.FilterSelected -> {
                _state.update { currentState ->
                    val newFiltered = if (intent.category == "All") {
                        currentState.products
                    } else {
                        currentState.products.filter { it.productType == intent.category }
                    }
                    currentState.copy(
                        selectedCategory = intent.category,
                        filteredProducts = newFiltered
                    )
                }
            }
            is WishlistIntent.ProductClicked -> {
                // Usually handled by side effects or UI directly
            }
        }
    }
}

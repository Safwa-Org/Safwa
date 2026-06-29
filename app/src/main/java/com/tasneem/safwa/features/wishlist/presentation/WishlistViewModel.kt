package com.tasneem.safwa.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    init {
        onIntent(WishlistIntent.LoadWishlist)
    }

    fun onIntent(intent: WishlistIntent) {
        when (intent) {
            is WishlistIntent.LoadWishlist -> {
                _state.update { it.copy(isLoading = true) }
                val mockProducts = WishlistMockData.products
                val categories = WishlistMockData.defaultCategories + mockProducts.map { it.productType }.distinct().sorted()
                
                _state.update { it.copy(
                    products = mockProducts,
                    filteredProducts = mockProducts,
                    categories = categories,
                    isLoading = false
                ) }
            }
            is WishlistIntent.ToggleFavorite -> {
                _state.update { currentState ->
                    val newProducts = currentState.products.filter { it.id != intent.product.id }
                    val newFiltered = if (currentState.selectedCategory == "All") {
                        newProducts
                    } else {
                        newProducts.filter { it.productType == currentState.selectedCategory }
                    }
                    val newCategories = WishlistMockData.defaultCategories + newProducts.map { it.productType }.distinct().sorted()
                    
                    currentState.copy(
                        products = newProducts,
                        filteredProducts = newFiltered,
                        categories = newCategories,
                        selectedCategory = if (newCategories.contains(currentState.selectedCategory)) currentState.selectedCategory else "All"
                    )
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

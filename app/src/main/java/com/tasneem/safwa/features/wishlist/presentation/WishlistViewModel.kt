package com.tasneem.safwa.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import com.tasneem.safwa.features.wishlist.domain.model.Product
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
        onEvent(WishlistEvent.LoadWishlist)
    }

    fun onEvent(event: WishlistEvent) {
        when (event) {
            is WishlistEvent.LoadWishlist -> {
                _state.update { it.copy(isLoading = true) }
                val mockProducts = listOf(
                    Product(
                        id = "1",
                        title = "Stylish Running Shoes",
                        handle = "stylish-running-shoes",
                        description = "Comfortable running shoes for daily use.",
                        vendor = "Nike",
                        productType = "Shoes",
                        price = "120.00",
                        currency = "USD",
                        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff",
                    ),
                    Product(
                        id = "2",
                        title = "Classic Leather Watch",
                        handle = "classic-leather-watch",
                        description = "A timeless leather watch.",
                        vendor = "Fossil",
                        productType = "Accessories",
                        price = "85.00",
                        currency = "USD",
                        imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d",
                    ),
                    Product(
                        id = "3",
                        title = "Wireless Headphones",
                        handle = "wireless-headphones",
                        description = "Noise-cancelling wireless headphones.",
                        vendor = "Sony",
                        productType = "Electronics",
                        price = "299.99",
                        currency = "USD",
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
                    )
                )
                
                val categories = listOf("All", "Fragrances", "Skincare") + mockProducts.map { it.productType }.distinct().sorted()
                
                _state.update { it.copy(
                    products = mockProducts,
                    filteredProducts = mockProducts,
                    categories = categories,
                    isLoading = false
                ) }
            }
            is WishlistEvent.ToggleFavorite -> {
                _state.update { currentState ->
                    val newProducts = currentState.products.filter { it.id != event.product.id }
                    val newFiltered = if (currentState.selectedCategory == "All") {
                        newProducts
                    } else {
                        newProducts.filter { it.productType == currentState.selectedCategory }
                    }
                    val newCategories = listOf("All", "Fragrances", "Skincare") + newProducts.map { it.productType }.distinct().sorted()
                    
                    currentState.copy(
                        products = newProducts,
                        filteredProducts = newFiltered,
                        categories = newCategories,
                        selectedCategory = if (newCategories.contains(currentState.selectedCategory)) currentState.selectedCategory else "All"
                    )
                }
            }
            is WishlistEvent.FilterSelected -> {
                _state.update { currentState ->
                    val newFiltered = if (event.category == "All") {
                        currentState.products
                    } else {
                        currentState.products.filter { it.productType == event.category }
                    }
                    currentState.copy(
                        selectedCategory = event.category,
                        filteredProducts = newFiltered
                    )
                }
            }
            is WishlistEvent.ProductClicked -> {
                // Usually handled by side effects or UI directly
            }
        }
    }
}

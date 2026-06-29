package com.tasneem.safwa.features.search.presentation

import androidx.lifecycle.ViewModel
import com.tasneem.safwa.features.wishlist.presentation.WishlistMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        val mockProducts = WishlistMockData.products
        val categories = WishlistMockData.defaultCategories + mockProducts.map { it.productType }.distinct().sorted()
        
        _state.update { it.copy(
            products = mockProducts,
            filteredProducts = mockProducts,
            categories = categories,
            isLoading = false
        ) }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _state.update { currentState ->
                    currentState.copy(searchQuery = event.query)
                }
                filterProducts()
            }
            is SearchEvent.FilterSelected -> {
                _state.update { currentState ->
                    currentState.copy(selectedCategory = event.category)
                }
                filterProducts()
            }
            is SearchEvent.ToggleFavorite -> {
                _state.update { currentState ->
                    val newProducts = currentState.products.map { 
                        if (it.id == event.product.id) {
                            it
                        } else {
                            it
                        }
                    }
                    currentState.copy(products = newProducts)
                }
            }
            is SearchEvent.ProductClicked -> {
            }
            is SearchEvent.ExecuteSearch -> {
                filterProducts()
            }
        }
    }

    private fun filterProducts() {
        _state.update { currentState ->
            val query = currentState.searchQuery.trim().lowercase()
            val category = currentState.selectedCategory

            val filtered = currentState.products.filter { product ->
                val matchesCategory = if (category == "All") true else product.productType == category
                val matchesQuery = if (query.isEmpty()) true else {
                    product.title.lowercase().contains(query) || product.vendor.lowercase().contains(query)
                }
                matchesCategory && matchesQuery
            }
            currentState.copy(filteredProducts = filtered)
        }
    }
}

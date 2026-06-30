package com.tasneem.safwa.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.features.wishlist.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.wishlist.presentation.WishlistMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
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

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.update { currentState ->
                    currentState.copy(searchQuery = intent.query)
                }
                filterProducts()
            }
            is SearchIntent.FilterSelected -> {
                _state.update { currentState ->
                    currentState.copy(selectedCategory = intent.category)
                }
                filterProducts()
            }
            is SearchIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.product)
                }
            }
            is SearchIntent.ProductClicked -> {
            }
            is SearchIntent.ExecuteSearch -> {
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

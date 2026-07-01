package com.tasneem.safwa.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import android.util.Log
import com.tasneem.safwa.features.search.domain.usecase.SearchProductsUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.category.domain.model.Category
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        _state.update { it.copy(
            isLoading = false
        ) }
        
        observeWishlist()
        loadCategories()
        performSearch("")
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

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.update { currentState ->
                    currentState.copy(searchQuery = intent.query)
                }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500.milliseconds)
                    performSearch(intent.query)
                }
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
                searchJob?.cancel()
                performSearch(_state.value.searchQuery)
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update { 
                it.copy(
                    products = emptyList(), 
                    filteredProducts = emptyList()
                ) 
            }
            return
        }
        viewModelScope.launch {
            searchProductsUseCase(query).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _state.update { 
                            it.copy(
                                isLoading = false, 
                                products = result.data,
                                categories =  it.categories
                            ) 
                        }
                        filterProducts()
                    }
                    is Resource.Error -> {
                        Log.e("SearchViewModel", "Search failed: ${result.message}")
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun filterProducts() {
        _state.update { currentState ->
            val category = currentState.selectedCategory
            val filtered = currentState.products.filter { product ->
                if (category?.handle == "all") true else product.productType == category?.handle
            }
            currentState.copy(filteredProducts = filtered)
        }
    }
}

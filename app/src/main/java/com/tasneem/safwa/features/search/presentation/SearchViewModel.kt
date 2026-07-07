package com.tasneem.safwa.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import android.util.Log
import com.tasneem.safwa.features.search.domain.usecase.SearchProductsUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.settings.languageandcurrency.data.CurrencyRateManager
import kotlinx.coroutines.flow.drop
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val currencyRateManager: CurrencyRateManager
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effect = Channel<SearchEffect>()
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null
    private var lastSearchedQuery: String? = null

    init {
        _state.update { it.copy(
            isLoading = false,
            currentCurrency = currencyRateManager.currentDisplayCurrency()
        ) }

        observeWishlist()
        loadCategories()
        executeSearch("")

        viewModelScope.launch {
            currencyRateManager.displayCurrency
                .drop(1)
                .collect { newCurrency ->
                    _state.update { it.copy(currentCurrency = newCurrency) }
                    executeSearch(_state.value.searchQuery)
                }
        }
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
                _state.update { it.copy(searchQuery = intent.query) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500.milliseconds)
                    executeSearch(intent.query)
                }
            }
            is SearchIntent.FilterSelected -> {
                _state.update { it.copy(selectedCategory = intent.category) }
                filterProducts()
            }
            is SearchIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.product)
                }
            }
            is SearchIntent.ProductClicked -> {
                viewModelScope.launch {
                    _effect.send(SearchEffect.NavigateToProductDetails(intent.product.handle))
                }
            }
            is SearchIntent.ExecuteSearch -> {
                executeSearch(_state.value.searchQuery)
            }
            is SearchIntent.ToggleFilterSheet -> {
                _state.update { it.copy(showFilterSheet = intent.show) }
            }
            is SearchIntent.UpdateSortOption -> {
                _state.update { it.copy(selectedSortOption = intent.option) }
            }
            is SearchIntent.ToggleBrandFilter -> {
                _state.update { currentState ->
                    val newBrands = currentState.selectedBrands.toMutableSet()
                    if (newBrands.contains(intent.brand)) newBrands.remove(intent.brand) else newBrands.add(intent.brand)
                    currentState.copy(selectedBrands = newBrands)
                }
            }
            is SearchIntent.ToggleSubCategoryFilter -> {
                _state.update { currentState ->
                    val newSubCategories = currentState.selectedSubCategories.toMutableSet()
                    if (newSubCategories.contains(intent.subCategory)) newSubCategories.remove(intent.subCategory) else newSubCategories.add(intent.subCategory)
                    currentState.copy(selectedSubCategories = newSubCategories)
                }
            }
            is SearchIntent.ToggleGroupBySubCategory -> {
                _state.update { it.copy(isGroupedBySubCategory = intent.enable) }
            }
            is SearchIntent.UpdatePriceRange -> {
                _state.update { it.copy(selectedPriceRange = intent.range) }
            }
            is SearchIntent.ApplyFilters -> {
                filterProducts()
                _state.update { it.copy(showFilterSheet = false) }
            }
            is SearchIntent.ClearFilters -> {
                _state.update { 
                    it.copy(
                        selectedBrands = emptySet(),
                        selectedSubCategories = emptySet(),
                        selectedSortOption = SortOption.NONE,
                        isGroupedBySubCategory = false,
                        selectedPriceRange = 0f..it.maxPrice
                    )
                }
                filterProducts()
            }
        }
    }

    private fun executeSearch(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            lastSearchedQuery = null
            _state.update {
                it.copy(
                    products = emptyList(),
                    filteredProducts = emptyList()
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            searchProductsUseCase(query).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val availableBrands = result.data.map { it.vendor }.distinct().filter { it.isNotBlank() }
                        val availableSubCategories = result.data.flatMap { it.tags }.distinct().filter { it.isNotBlank() }
                        val productsMaxPrice = result.data.maxOfOrNull { it.price.toFloatOrNull() ?: 0f } ?: 1000f

                        _state.update { currentState ->
                            val effectiveMax = maxOf(productsMaxPrice, currentState.selectedPriceRange.endInclusive)

                            currentState.copy(
                                isLoading = false,
                                products = result.data,
                                categories = currentState.categories,
                                availableBrands = availableBrands,
                                availableSubCategories = availableSubCategories,
                                maxPrice = effectiveMax
                            )
                        }
                        lastSearchedQuery = query
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
            val selectedBrands = currentState.selectedBrands
            val selectedSubCategories = currentState.selectedSubCategories
            
            var filtered = currentState.products.filter { product ->
                val matchesCategory = if (category?.handle == "all" || category == null) true else product.productType == category.handle
                val matchesBrand = if (selectedBrands.isEmpty()) true else selectedBrands.contains(product.vendor)
                val matchesSubCategory = if (selectedSubCategories.isEmpty()) true else product.tags.any { selectedSubCategories.contains(it) }
                
                val productPrice = product.price.toFloatOrNull() ?: 0f
                val matchesPrice = productPrice in currentState.selectedPriceRange
                
                matchesCategory && matchesBrand && matchesSubCategory && matchesPrice
            }
            
            filtered = when (currentState.selectedSortOption) {
                SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
                SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
                SortOption.BEST_SELLER -> filtered.shuffled() // Placeholder for Best Seller
                SortOption.NONE -> filtered
            }
            
            currentState.copy(filteredProducts = filtered)
        }
    }
}

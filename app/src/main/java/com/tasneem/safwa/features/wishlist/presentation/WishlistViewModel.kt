package com.tasneem.safwa.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetCategoriesUseCase
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.settings.languageandcurrency.data.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    private val _effect = Channel<WishlistEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onIntent(WishlistIntent.LoadWishlist)
        observeWishlist()
        loadCategories()

        viewModelScope.launch {
            languageManager.displayLanguage
                .drop(1)
                .collect {
                    loadCategories()
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
                when (result) {
                    is Resource.Success -> {
                        val products = result.data
                        _state.update { currentState ->
                            val categories = currentState.categories
                            val selectedCategory = currentState.selectedCategory ?: Category(id = "all", title = "All", handle = "all", imageUrl = null)
                            val filteredProducts = if (selectedCategory.handle == "all") {
                                products
                            } else {
                                products.filter { it.productType == selectedCategory.handle }
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
                    val newFiltered = if (intent.category.handle == "all") {
                        currentState.products
                    } else {
                        currentState.products.filter { it.productType == intent.category.handle }
                    }
                    currentState.copy(
                        selectedCategory = intent.category,
                        filteredProducts = newFiltered
                    )
                }
            }
            is WishlistIntent.ProductClicked -> {
                viewModelScope.launch {
                    _effect.send(WishlistEffect.NavigateToProductDetails(intent.product.handle))
                }
            }
        }
    }
}

package com.tasneem.safwa.features.latest_products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.usecase.GetLatestProductsPagerUseCase
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LatestProductsViewModel @Inject constructor(
    getLatestProductsPagerUseCase: GetLatestProductsPagerUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase
) : ViewModel() {

    val productsPagingFlow: Flow<PagingData<Product>> = getLatestProductsPagerUseCase()
        .cachedIn(viewModelScope)

    private val _favoriteProductIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteProductIds: StateFlow<Set<String>> = _favoriteProductIds

    init {
        viewModelScope.launch {
            getWishlistUseCase().collect { result ->
                if (result is Resource.Success) {
                    _favoriteProductIds.value = result.data.map { it.id }.toSet()
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

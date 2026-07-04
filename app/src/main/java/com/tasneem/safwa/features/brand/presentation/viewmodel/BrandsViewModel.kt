package com.tasneem.safwa.features.brand.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.brand.presentation.state.BrandsUiEffect
import com.tasneem.safwa.features.brand.presentation.state.BrandsUiIntent
import com.tasneem.safwa.features.brand.presentation.state.BrandsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BrandsUiState())
    val uiState: StateFlow<BrandsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<BrandsUiEffect>()
    val uiEffect: SharedFlow<BrandsUiEffect> = _uiEffect.asSharedFlow()

    init {
        handleIntent(BrandsUiIntent.LoadBrands)
    }

    fun handleIntent(intent: BrandsUiIntent) {
        when (intent) {
            is BrandsUiIntent.LoadBrands -> loadBrands()
            is BrandsUiIntent.OnBrandClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(BrandsUiEffect.NavigateToBrandDetails(intent.brand.name))
                }
            }

            is BrandsUiIntent.OnBackClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(BrandsUiEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val brands = listOf(
                    Brand("Maison"), Brand("Atelier"),
                    Brand("Noir"), Brand("Lumière"),
                    Brand("Riad"), Brand("Halcyon")
                )
                _uiState.update { it.copy(isLoading = false, brands = brands) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}
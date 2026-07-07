package com.tasneem.safwa.features.settings.orderhistory.presentation

data class OrderDetailsUiState(
    val showCancelConfirm: Boolean = false,
    val isCancelling: Boolean = false,
    val cancelError: String? = null
)

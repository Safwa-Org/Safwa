package com.tasneem.safwa.features.aichat.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product

sealed interface AiChatEvent {
    data class InputChanged(val text: String) : AiChatEvent
    data object SendMessage : AiChatEvent
    data class SuggestionChipClicked(val chip: String) : AiChatEvent
    data object DismissChat : AiChatEvent
    data class ToggleFavorite(val product: Product) : AiChatEvent
    data class ProductClicked(val product: Product) : AiChatEvent
}

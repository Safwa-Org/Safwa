package com.tasneem.safwa.features.aichat.presentation.state

sealed interface AiChatEvent {
    data class InputChanged(val text: String) : AiChatEvent
    data object SendMessage : AiChatEvent
    data class SuggestionChipClicked(val chip: String) : AiChatEvent
    data object DismissChat : AiChatEvent
}

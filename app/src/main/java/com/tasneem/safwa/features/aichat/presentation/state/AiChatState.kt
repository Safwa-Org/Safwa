package com.tasneem.safwa.features.aichat.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val products: List<Product> = emptyList()
)

data class AiChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val inputText: String = "",
    val suggestionChips: List<String> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)


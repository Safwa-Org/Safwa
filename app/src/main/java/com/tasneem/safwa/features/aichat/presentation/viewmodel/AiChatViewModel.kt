package com.tasneem.safwa.features.aichat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.aichat.presentation.state.AiChatEvent
import com.tasneem.safwa.features.aichat.presentation.state.AiChatState
import com.tasneem.safwa.features.aichat.presentation.state.ChatMessage
import com.tasneem.safwa.features.aichat.domain.usecase.AiSearchProductsUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AiChatEffect {
    data class NavigateToProductDetails(val handle: String) : AiChatEffect
}

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiSearchProductsUseCase: AiSearchProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase
) : ViewModel() {

    private val _effect = Channel<AiChatEffect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(
        AiChatState(
            suggestionChips = listOf(
                "Gift under SAR 300",
                "Running shoes",
            )
        )
    )
    val state: StateFlow<AiChatState> = _state.asStateFlow()

    init {
        loadUserGreeting()
        observeWishlist()
    }

    private fun loadUserGreeting() {
        viewModelScope.launch {
            val userName = when (val result = getCurrentUserUseCase()) {
                is Resource.Success -> result.data?.firstName ?: "there"
                else -> "there"
            }
            val greetingMessage = ChatMessage(
                text = "Hi $userName ✦ I'm Safwa, your personal shopper. What are you looking for today?",
                isFromUser = false
            )
            _state.update { it.copy(messages = listOf(greetingMessage)) }
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

    fun onEvent(event: AiChatEvent) {
        when (event) {
            is AiChatEvent.InputChanged -> {
                _state.update { it.copy(inputText = event.text) }
            }

            is AiChatEvent.SendMessage -> {
                val text = _state.value.inputText.trim()
                if (text.isBlank()) return
                sendUserMessage(text)
            }

            is AiChatEvent.SuggestionChipClicked -> {
                sendUserMessage(event.chip)
            }

            is AiChatEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.product)
                }
            }

            is AiChatEvent.ProductClicked -> {
                viewModelScope.launch {
                    _effect.send(AiChatEffect.NavigateToProductDetails(event.product.handle))
                }
            }

            is AiChatEvent.DismissChat -> {
            }
        }
    }

    private fun sendUserMessage(text: String) {
        val userMessage = ChatMessage(text = text, isFromUser = true)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isTyping = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            aiSearchProductsUseCase(text).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isTyping = true) }
                    }
                    is Resource.Success -> {
                        val products = result.data
                        val aiMessage = ChatMessage(
                            text = if (products.isNotEmpty()) "Here's what I found for you ✦" else "I couldn't find any products matching your request.",
                            isFromUser = false,
                            products = products
                        )
                        _state.update {
                            it.copy(
                                messages = it.messages + aiMessage,
                                isTyping = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        val aiMessage = ChatMessage(
                            text = "Sorry, I encountered an issue while searching. Please try again.",
                            isFromUser = false
                        )
                        _state.update {
                            it.copy(
                                messages = it.messages + aiMessage,
                                isTyping = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}

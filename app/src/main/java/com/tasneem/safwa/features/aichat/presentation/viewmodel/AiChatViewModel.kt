package com.tasneem.safwa.features.aichat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.features.aichat.presentation.state.AiChatEvent
import com.tasneem.safwa.features.aichat.presentation.state.AiChatState
import com.tasneem.safwa.features.aichat.presentation.state.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        AiChatState(
            messages = listOf(
                ChatMessage(
                    text = "Hi Aisha ✦ I'm Safwa, your personal shopper. What are you looking for today?",
                    isFromUser = false
                )
            ),
            suggestionChips = listOf(
                "Gift under SAR 300",
                "New arrivals",
                "Match with my style"
            )
        )
    )
    val state: StateFlow<AiChatState> = _state.asStateFlow()

    private val dummyResponses = listOf(
        "fragrance" to "Nuit d'Or by Maison fits perfectly — amber, oud, a whisper of rose. SAR 480. Want me to add it to your cart?",
        "gift" to "How about a Vermilion Bifold Wallet by Atelier? Premium leather, beautifully boxed — SAR 320. Perfect for gifting.",
        "watch" to "The Onyx Chrono by Noir is stunning — Swiss movement, sapphire crystal. SAR 2,150. Shall I show you more details?",
        "new" to "We just got the Halcyon Round sunglasses by Lumière — polarized lenses, titanium frame. SAR 540. Want to take a look?",
        "leather" to "Our Atelier collection has some beautiful pieces — the Vermilion Bifold at SAR 320 is a best-seller. Interested?",
        "style" to "Based on your purchase history, I'd recommend the Nuit d'Or fragrance (SAR 480) paired with the Onyx Chrono watch (SAR 2,150). A bold, refined combination.",
        "cart" to "Sure! I've added it to your cart. You now have 3 items totaling SAR 1,280. Ready to checkout?",
        "hello" to "Hello! 👋 I'm here to help you find the perfect luxury items. What catches your eye today?",
        "hi" to "Hey there! ✦ Looking for something special today? I can help with fragrances, leather goods, watches, and more."
    )

    private val fallbackResponse = "That's a great question! Let me look into our collection for you. In the meantime, would you like to explore our best sellers or new arrivals?"

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
                isTyping = true
            )
        }

        viewModelScope.launch {
            delay(1200L + (Math.random() * 800).toLong())
            val lowerText = text.lowercase()
            val response = dummyResponses
                .firstOrNull { (keyword, _) -> lowerText.contains(keyword) }
                ?.second ?: fallbackResponse
            val aiMessage = ChatMessage(text = response, isFromUser = false)
            _state.update {
                it.copy(
                    messages = it.messages + aiMessage,
                    isTyping = false
                )
            }
        }
    }
}

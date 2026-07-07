package com.tasneem.safwa.features.aichat.domain.model

data class SmartSearchQuery(
    val query: String,
    val first: Int = 20,
    val messageIfFound: String = "Here's what I found for you ✦",
    val messageIfNotFound: String = "I couldn't find any products matching your request."
)

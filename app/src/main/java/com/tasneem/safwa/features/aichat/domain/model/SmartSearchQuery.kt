package com.tasneem.safwa.features.aichat.domain.model

data class SmartSearchQuery(
    val query: String,
    val first: Int = 20
)

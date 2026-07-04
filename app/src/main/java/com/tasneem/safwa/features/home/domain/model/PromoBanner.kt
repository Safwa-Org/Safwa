package com.tasneem.safwa.features.home.domain.model

import androidx.compose.ui.graphics.Color

data class PromoBanner(
    val id: String,
    val tagLabel: String,
    val headline: String,
    val promoCode: String,
    val imageUrl: String,
    val backgroundColor: Color,
    val contentColor: Color
)

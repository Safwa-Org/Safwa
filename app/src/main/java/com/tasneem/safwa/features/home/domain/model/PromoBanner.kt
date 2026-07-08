package com.tasneem.safwa.features.home.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class PromoBanner(
    val id: String,
    @StringRes val tagLabel: Int,
    @StringRes val headline: Int,
    val promoCode: String,
    val imageUrl: String,
    val backgroundColor: Color,
    val contentColor: Color
)

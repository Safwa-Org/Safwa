package com.tasneem.safwa.features.productdetails.presentation.state.mapper

import androidx.compose.ui.graphics.Color

fun String.toDisplayColor(): Color? {
    val normalized = trim()
    if (normalized.isEmpty()) return null

    parseHexColor(normalized)?.let { return it }

    return namedColors[normalized.lowercase().replace(" ", "").replace("-", "")]
}

private fun parseHexColor(value: String): Color? {
    val hex = value.removePrefix("#")
    if (hex.length != 6 && hex.length != 8) return null
    val parsed = hex.toLongOrNull(16) ?: return null
    return when (hex.length) {
        6 -> Color(0xFF000000 or parsed)
        else -> Color(parsed)
    }
}

private val namedColors: Map<String, Color> = mapOf(
    "black" to Color(0xFF000000),
    "white" to Color(0xFFFFFFFF),
    "gray" to Color(0xFF808080),
    "grey" to Color(0xFF808080),
    "silver" to Color(0xFFC0C0C0),
    "red" to Color(0xFFF44336),
    "maroon" to Color(0xFF800000),
    "pink" to Color(0xFFE91E63),
    "rose" to Color(0xFFF06292),
    "orange" to Color(0xFFFF9800),
    "coral" to Color(0xFFFF7F50),
    "brown" to Color(0xFF795548),
    "tan" to Color(0xFFD2B48C),
    "beige" to Color(0xFFF5F5DC),
    "khaki" to Color(0xFFBDB76B),
    "cream" to Color(0xFFFFFDD0),
    "gold" to Color(0xFFFFD700),
    "yellow" to Color(0xFFFFEB3B),
    "olive" to Color(0xFF808000),
    "green" to Color(0xFF4CAF50),
    "lime" to Color(0xFFCDDC39),
    "mint" to Color(0xFF98FF98),
    "teal" to Color(0xFF009688),
    "cyan" to Color(0xFF00BCD4),
    "turquoise" to Color(0xFF40E0D0),
    "blue" to Color(0xFF2196F3),
    "navy" to Color(0xFF000080),
    "navyblue" to Color(0xFF000080),
    "skyblue" to Color(0xFF87CEEB),
    "lightblue" to Color(0xFFADD8E6),
    "royalblue" to Color(0xFF4169E1),
    "indigo" to Color(0xFF3F51B5),
    "purple" to Color(0xFF9C27B0),
    "violet" to Color(0xFF8F00FF),
    "lavender" to Color(0xFFE6E6FA),
    "magenta" to Color(0xFFFF00FF),
)

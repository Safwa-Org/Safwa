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
    // Neutrals
    "black" to Color(0xFF000000),
    "jetblack" to Color(0xFF0A0A0A),
    "white" to Color(0xFFFFFFFF),
    "offwhite" to Color(0xFFFAF9F6),
    "ivory" to Color(0xFFFFFFF0),
    "gray" to Color(0xFF808080),
    "grey" to Color(0xFF808080),
    "lightgray" to Color(0xFFD3D3D3),
    "lightgrey" to Color(0xFFD3D3D3),
    "darkgray" to Color(0xFFA9A9A9),
    "darkgrey" to Color(0xFFA9A9A9),
    "heathergray" to Color(0xFFB6B6B6),
    "heathergrey" to Color(0xFFB6B6B6),
    "charcoal" to Color(0xFF36454F),
    "slate" to Color(0xFF708090),
    "silver" to Color(0xFFC0C0C0),

    // Reds / pinks
    "red" to Color(0xFFF44336),
    "darkred" to Color(0xFF8B0000),
    "crimson" to Color(0xFFDC143C),
    "scarlet" to Color(0xFFFF2400),
    "maroon" to Color(0xFF800000),
    "burgundy" to Color(0xFF800020),
    "wine" to Color(0xFF722F37),
    "pink" to Color(0xFFE91E63),
    "hotpink" to Color(0xFFFF69B4),
    "lightpink" to Color(0xFFFFB6C1),
    "fuchsia" to Color(0xFFFF00FF),
    "salmon" to Color(0xFFFA8072),
    "rose" to Color(0xFFF06292),

    // Oranges / browns / earth
    "orange" to Color(0xFFFF9800),
    "darkorange" to Color(0xFFFF8C00),
    "coral" to Color(0xFFFF7F50),
    "peach" to Color(0xFFFFDAB9),
    "apricot" to Color(0xFFFBCEB1),
    "brown" to Color(0xFF795548),
    "chocolate" to Color(0xFF7B3F00),
    "tan" to Color(0xFFD2B48C),
    "camel" to Color(0xFFC19A6B),
    "beige" to Color(0xFFF5F5DC),
    "khaki" to Color(0xFFBDB76B),
    "cream" to Color(0xFFFFFDD0),
    "rust" to Color(0xFFB7410E),
    "terracotta" to Color(0xFFE2725B),

    // Yellows / golds
    "gold" to Color(0xFFFFD700),
    "yellow" to Color(0xFFFFEB3B),
    "mustard" to Color(0xFFFFDB58),
    "amber" to Color(0xFFFFBF00),

    // Greens
    "olive" to Color(0xFF808000),
    "green" to Color(0xFF4CAF50),
    "darkgreen" to Color(0xFF006400),
    "lightgreen" to Color(0xFF90EE90),
    "forestgreen" to Color(0xFF228B22),
    "emerald" to Color(0xFF50C878),
    "sage" to Color(0xFF9CAF88),
    "lime" to Color(0xFFCDDC39),
    "mint" to Color(0xFF98FF98),
    "teal" to Color(0xFF009688),

    // Blues / cyans
    "cyan" to Color(0xFF00BCD4),
    "turquoise" to Color(0xFF40E0D0),
    "aqua" to Color(0xFF00FFFF),
    "blue" to Color(0xFF2196F3),
    "darkblue" to Color(0xFF00008B),
    "navy" to Color(0xFF000080),
    "navyblue" to Color(0xFF000080),
    "skyblue" to Color(0xFF87CEEB),
    "lightblue" to Color(0xFFADD8E6),
    "royalblue" to Color(0xFF4169E1),
    "denim" to Color(0xFF1560BD),
    "cobalt" to Color(0xFF0047AB),
    "tealblue" to Color(0xFF367588),

    // Purples
    "indigo" to Color(0xFF3F51B5),
    "purple" to Color(0xFF9C27B0),
    "violet" to Color(0xFF8F00FF),
    "plum" to Color(0xFF8E4585),
    "mauve" to Color(0xFFE0B0FF),
    "lilac" to Color(0xFFC8A2C8),
    "lavender" to Color(0xFFE6E6FA),
    "magenta" to Color(0xFFFF00FF),
)

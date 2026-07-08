package com.tasneem.safwa.features.productdetails.presentation.view.component

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.VariantOptionGroup
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.toDisplayColor
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.VariantOptionValue

@Composable
fun ProductInfoSection(
    vendor: String,
    productType: String,
    title: String,
    priceFormatted: String,
    priceRangeFormatted: String?,
    variantOptions: List<VariantOptionGroup>,
    selectedOptions: Map<String, String>,
    onOptionSelected: (optionName: String, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = vendor,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
            if (productType.isNotBlank()) {
                Text(
                    text = "·",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = productType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = priceRangeFormatted ?: priceFormatted,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        variantOptions.forEach { group ->
            VariantOptionGroupSection(
                group = group,
                selectedValue = selectedOptions[group.name].orEmpty(),
                onValueSelected = { value -> onOptionSelected(group.name, value) }
            )
        }
    }
}

@Composable
private fun VariantOptionGroupSection(
    group: VariantOptionGroup,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            group.values.forEach { option ->
                val swatchColor = if (group.isColorGroup) option.value.toDisplayColor() else null
                if (swatchColor != null) {
                    ColorSwatch(
                        color = swatchColor,
                        contentDescription = option.value,
                        isSelected = option.value == selectedValue,
                        isAvailable = option.isAvailable,
                        onClick = { onValueSelected(option.value) }
                    )
                } else {
                    VariantChip(
                        option = option,
                        isSelected = option.value == selectedValue,
                        onClick = { onValueSelected(option.value) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    contentDescription: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
) {
    val unavailableAlpha = 0.35f
    val swatchAlpha = if (isAvailable) 1f else unavailableAlpha
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline.copy(alpha = swatchAlpha)
                ),
                shape = CircleShape
            )
            .padding(4.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = swatchAlpha))
            .clickable(enabled = isAvailable) { onClick() }
            .semantics { this.contentDescription = contentDescription },
    )
}

@Composable
private fun VariantChip(
    option: VariantOptionValue,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val unavailableAlpha = 0.35f
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(36.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    !option.isAvailable -> MaterialTheme.colorScheme.surface.copy(alpha = unavailableAlpha)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                border = BorderStroke(
                    1.dp,
                    if (!option.isAvailable)
                        MaterialTheme.colorScheme.outline.copy(alpha = unavailableAlpha)
                    else
                        MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(36.dp)
            )
            .clickable(enabled = option.isAvailable) { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option.value,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (!option.isAvailable) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !option.isAvailable -> MaterialTheme.colorScheme.onSurface.copy(alpha = unavailableAlpha)
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            }
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductInfoSectionPreview() {
    SafwaTheme {
        ProductInfoSection(
            vendor = "Nike",
            productType = "Running Shoes",
            title = "Nike Air Max 270",
            priceFormatted = "USD 149.99",
            priceRangeFormatted = "USD 149.99 – 189.99",
            variantOptions = listOf(
                VariantOptionGroup(
                    name = "Size",
                    values = listOf(
                        VariantOptionValue("S", true),
                        VariantOptionValue("M", true),
                        VariantOptionValue("L", false),
                        VariantOptionValue("XL", true),
                    )
                ),
                VariantOptionGroup(
                    name = "Color",
                    values = listOf(
                        VariantOptionValue("Black", true),
                        VariantOptionValue("White", false),
                        VariantOptionValue("Red", true),
                    ),
                    isColorGroup = true,
                )
            ),
            selectedOptions = mapOf("Size" to "M", "Color" to "Black"),
            onOptionSelected = { _, _ -> }
        )
    }
}
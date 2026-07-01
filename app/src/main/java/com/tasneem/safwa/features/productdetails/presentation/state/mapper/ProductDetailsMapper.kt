package com.tasneem.safwa.features.productdetails.presentation.state.mapper

import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.model.ProductVariant

data class ProductDetailsUiModel(
    val id: String,
    val title: String,
    val vendor: String,
    val productType: String,
    val description: String,
    val priceFormatted: String,
    val priceRangeFormatted: String?,
    val imageUrls: List<String>,
    val imageLabels: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val variantOptions: List<VariantOptionGroup>,
    val variants: List<ProductVariant>,
)

data class VariantOptionValue(
    val value: String,
    val isAvailable: Boolean,
)

data class VariantOptionGroup(
    val name: String,
    val values: List<VariantOptionValue>,
)

fun ProductDetails.toUiModel() = ProductDetailsUiModel(
    id = id,
    title = title,
    vendor = vendor,
    productType = productType,
    description = description,
    priceFormatted = "${priceRange.min.currency} ${priceRange.min.amount}",
    priceRangeFormatted = if (priceRange.min.amount != priceRange.max.amount)
        "${priceRange.min.currency} ${priceRange.min.amount} – ${priceRange.max.amount}"
    else null,
    imageUrls = images.map { it.url },
    imageLabels = images.map { it.altText },
    rating = 4.9f,
    reviewCount = 286,
    variantOptions = buildVariantOptions(variants),
    variants = variants,
)

private fun buildVariantOptions(variants: List<ProductVariant>): List<VariantOptionGroup> {
    val optionNames = variants
        .flatMap { it.selectedOptions }
        .map { it.name }
        .distinct()

    return optionNames.map { name ->
        val values = variants
            .groupBy { variant ->
                variant.selectedOptions.firstOrNull { it.name == name }?.value
            }
            .filterKeys { it != null }
            .map { (value, matchingVariants) ->
                VariantOptionValue(
                    value = value!!,
                    isAvailable = matchingVariants.any { it.availableForSale },
                )
            }
        VariantOptionGroup(name = name, values = values)
    }
}

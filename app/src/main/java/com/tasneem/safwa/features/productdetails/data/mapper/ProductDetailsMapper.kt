package com.tasneem.safwa.features.productdetails.data.mapper

import com.tasneem.network.dto.ImageNode
import com.tasneem.network.dto.Money
import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductPriceRange
import com.tasneem.network.dto.ProductVariantNode
import com.tasneem.safwa.features.productdetails.domain.model.Price
import com.tasneem.safwa.features.productdetails.domain.model.PriceRange
import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.model.ProductImage
import com.tasneem.safwa.features.productdetails.domain.model.ProductVariant
import com.tasneem.safwa.features.productdetails.domain.model.SelectedOption
import com.tasneem.network.dto.SelectedOption as RemoteSelectedOption

fun ProductDetailsDto.toDomain() = ProductDetails(
    id = id,
    title = title,
    handle = handle,
    vendor = vendor,
    description = description.orEmpty(),
    descriptionHtml = descriptionHtml.orEmpty(),
    productType = productType,
    priceRange = priceRange.toDomain(),
    images = images.edges.map { it.node.toDomain() },
    variants = variants.edges.map { it.node.toDomain() }
)

private fun ProductPriceRange.toDomain() = PriceRange(
    min = minVariantPrice.toDomain(),
    max = maxVariantPrice.toDomain()
)

private fun Money.toDomain() = Price(
    amount = amount,
    currency = currencyCode
)

private fun ImageNode.toDomain() = ProductImage(
    id = id,
    url = url,
    altText = altText.orEmpty(),
    width = width,
    height = height
)

private fun ProductVariantNode.toDomain() = ProductVariant(
    id = id,
    title = title,
    availableForSale = availableForSale,
    selectedOptions = selectedOptions.map { it.toDomain() },
    price = price.toDomain(),
    quantityAvailable = quantityAvailable
)

private fun RemoteSelectedOption.toDomain() = SelectedOption(
    name = name,
    value = value
)
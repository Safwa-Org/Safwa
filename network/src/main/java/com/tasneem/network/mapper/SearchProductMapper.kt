package com.tasneem.network.mapper

import com.tasneem.network.dto.ProductDto
import com.tasneem.safwa.network.SearchProductsQuery
import javax.inject.Inject

class SearchProductMapper @Inject constructor() : Mapper<SearchProductsQuery.Node, ProductDto> {
    override fun map(input: SearchProductsQuery.Node) = ProductDto(
        id = input.id,
        title = input.title,
        handle = input.handle,
        description = input.description,
        productType = input.productType,
        vendor = input.vendor,
        price = input.priceRange.minVariantPrice.amount.toString(),
        currency = input.priceRange.minVariantPrice.currencyCode.name,
        imageUrls = input.images.edges.map { it.node.url.toString() },
        imageAlts = input.images.edges.map { it.node.altText }
    )
}

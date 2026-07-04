package com.tasneem.network.mapper

import com.tasneem.network.dto.ProductDto
import com.tasneem.safwa.network.GetProductsForVendorQuery
import javax.inject.Inject

class VendorProductMapper @Inject constructor() : Mapper<GetProductsForVendorQuery.Node, ProductDto> {
    override fun map(input: GetProductsForVendorQuery.Node) = ProductDto(
        id = input.id,
        title = input.title,
        handle = input.handle,
        description = null,
        productType = "",
        vendor = input.vendor,
        price = input.priceRange.minVariantPrice.amount.toString(),
        currency = input.priceRange.minVariantPrice.currencyCode.name,
        imageUrls = input.images.edges.map { it.node.url.toString() },
        imageAlts = input.images.edges.map { it.node.altText }
    )
}

package com.tasneem.network.mapper

import com.tasneem.network.dto.CollectionConnection
import com.tasneem.network.dto.CollectionEdge
import com.tasneem.network.dto.CollectionNode
import com.tasneem.network.dto.ImageConnection
import com.tasneem.network.dto.ImageEdge
import com.tasneem.network.dto.ImageNode
import com.tasneem.network.dto.Money
import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductPriceRange
import com.tasneem.network.dto.ProductVariantConnection
import com.tasneem.network.dto.ProductVariantEdge
import com.tasneem.network.dto.ProductVariantNode
import com.tasneem.network.dto.SelectedOption
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.safwa.network.GetProductByHandleQuery
import javax.inject.Inject

class ProductDetailsDtoMapper @Inject constructor() :
    Mapper<GetProductByHandleQuery.Data, ProductDetailsDto?> {

    override fun map(input: GetProductByHandleQuery.Data): ProductDetailsDto {
        val apolloProduct = input.product
            ?: throw EmptyResponseException()

        return mapProduct(apolloProduct)
    }

    private fun mapProduct(apolloProduct: GetProductByHandleQuery.Product): ProductDetailsDto {
        return ProductDetailsDto(
            id = apolloProduct.id,
            title = apolloProduct.title,
            handle = apolloProduct.handle,
            description = apolloProduct.description,
            descriptionHtml = apolloProduct.descriptionHtml.toString(),
            vendor = apolloProduct.vendor,
            productType = apolloProduct.productType,
            collections = mapCollections(apolloProduct.collections),
            priceRange = mapPriceRange(apolloProduct.priceRange),
            images = mapImages(apolloProduct.images),
            variants = mapVariants(apolloProduct.variants)
        )
    }

    private fun mapCollections(collections: GetProductByHandleQuery.Collections): CollectionConnection {
        return CollectionConnection(
            edges = collections.edges.map { edge ->
                CollectionEdge(
                    node = CollectionNode(
                        id = edge.node.id,
                        title = edge.node.title,
                        handle = edge.node.handle
                    )
                )
            }
        )
    }

    private fun mapPriceRange(priceRange: GetProductByHandleQuery.PriceRange): ProductPriceRange {
        return ProductPriceRange(
            minVariantPrice = Money(
                amount = priceRange.minVariantPrice.amount.toString(),
                currencyCode = priceRange.minVariantPrice.currencyCode.toString()
            ),
            maxVariantPrice = Money(
                amount = priceRange.maxVariantPrice.amount.toString(),
                currencyCode = priceRange.maxVariantPrice.currencyCode.toString()
            )
        )
    }

    private fun mapImages(images: GetProductByHandleQuery.Images): ImageConnection {
        return ImageConnection(
            edges = images.edges.map { edge ->
                ImageEdge(
                    node = ImageNode(
                        id = edge.node.id ?: "",
                        url = edge.node.url.toString(),
                        altText = edge.node.altText,
                        width = edge.node.width,
                        height = edge.node.height
                    )
                )
            }
        )
    }

    private fun mapVariants(variants: GetProductByHandleQuery.Variants): ProductVariantConnection {
        return ProductVariantConnection(
            edges = variants.edges.map { edge ->
                ProductVariantEdge(
                    node = ProductVariantNode(
                        id = edge.node.id,
                        title = edge.node.title,
                        availableForSale = edge.node.availableForSale,
                        selectedOptions = edge.node.selectedOptions.map { option ->
                            SelectedOption(
                                name = option.name,
                                value = option.value
                            )
                        },
                        price = Money(
                            amount = edge.node.price.amount.toString(),
                            currencyCode = edge.node.price.currencyCode.toString()
                        ),
                        quantityAvailable = edge.node.quantityAvailable
                    )
                )
            }
        )
    }
}
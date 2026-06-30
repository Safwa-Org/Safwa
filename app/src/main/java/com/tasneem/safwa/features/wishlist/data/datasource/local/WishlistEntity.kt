package com.tasneem.safwa.features.wishlist.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tasneem.safwa.features.core.domain.model.Product

@Entity(tableName = "wishlist_table")
data class WishlistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val handle: String,
    val description: String,
    val vendor: String,
    val productType: String,
    val price: String,
    val currency: String,
    val imageUrl: List<String>,
    val imageAltText: List<String>
) {
    fun toDomainModel(): Product = Product(
        id = id,
        title = title,
        handle = handle,
        description = description,
        vendor = vendor,
        productType = productType,
        price = price,
        currency = currency,
        imageUrl = imageUrl,
        imageAltText = imageAltText
    )
}

fun Product.toEntity(): WishlistEntity = WishlistEntity(
    id = id,
    title = title,
    handle = handle,
    description = description,
    vendor = vendor,
    productType = productType,
    price = price,
    currency = currency,
    imageUrl = imageUrl,
    imageAltText = imageAltText
)

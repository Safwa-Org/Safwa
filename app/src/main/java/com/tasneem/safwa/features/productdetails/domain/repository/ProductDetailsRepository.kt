package com.tasneem.safwa.features.productdetails.domain.repository

import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails

interface ProductDetailsRepository {

    suspend fun getProductDetailsByHandle(handle: String): ProductDetails

    suspend fun toggleWishlist(productId: String, isWishlist: Boolean): Boolean
}
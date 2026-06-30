package com.tasneem.network.datasource.product

import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductDto

interface ProductRemoteDataSource {

    suspend fun getProducts(page: Int): List<ProductDto>

    suspend fun searchProducts(query: String, first: Int): List<ProductDto>

    suspend fun getProductDetailsByHandle(handle: String): ProductDetailsDto
}
package com.tasneem.network.datasource.product

import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.dto.CategoryDto

interface ProductRemoteDataSource {

    suspend fun getProducts(first: Int, after: String? = null, sortKey: String? = null, languageCode: String): List<ProductDto>

    suspend fun searchProducts(query: String, first: Int, languageCode: String): List<ProductDto>

    suspend fun getProductDetailsByHandle(handle: String, languageCode: String): ProductDetailsDto

    suspend fun getCategories(first: Int = 100, languageCode: String): List<CategoryDto>

    suspend fun getCollectionProducts(handle: String, first: Int = 20, languageCode: String): List<ProductDto>
}
package com.tasneem.network.datasource.product

import com.tasneem.network.dto.ProductDto

interface ProductRemoteDataSource {

    suspend fun getProducts(page: Int): List<ProductDto>

}
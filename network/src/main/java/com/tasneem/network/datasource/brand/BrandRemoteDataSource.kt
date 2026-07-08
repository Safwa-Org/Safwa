package com.tasneem.network.datasource.brand

import com.tasneem.network.dto.ProductDto

interface BrandRemoteDataSource {

    suspend fun getProductVendors(pageSize: Int = 250, languageCode: String): List<String>

    suspend fun getProductsForVendor(vendor: String, pageSize: Int = 250, languageCode: String): List<ProductDto>
}

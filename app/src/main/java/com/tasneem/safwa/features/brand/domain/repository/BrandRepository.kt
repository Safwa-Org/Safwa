package com.tasneem.safwa.features.brand.domain.repository

import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.core.domain.model.Product

interface BrandRepository {
    suspend fun getBrands(): List<Brand>
    suspend fun getProductsByBrand(brandName: String): List<Product>
}

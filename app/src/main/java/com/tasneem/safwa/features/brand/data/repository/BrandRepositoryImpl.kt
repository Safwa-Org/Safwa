package com.tasneem.safwa.features.brand.data.repository

import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val remoteDataSource: BrandRemoteDataSource
) : BrandRepository {

    private var cachedBrands: List<Brand>? = null

    override suspend fun getBrands(): List<Brand> {
        cachedBrands?.let { return it }
        return safeCall {
            val brands = remoteDataSource.getProductVendors()
                .filter { it.isNotBlank() }
                .distinct()
                .sortedBy { it.lowercase() }
                .map { Brand(name = it) }
            cachedBrands = brands
            brands
        }
    }

    override suspend fun getProductsByBrand(brandName: String): List<Product> {
        return safeCall {
            remoteDataSource.getProductsForVendor(brandName).map { it.toDomainModel() }
        }
    }
}

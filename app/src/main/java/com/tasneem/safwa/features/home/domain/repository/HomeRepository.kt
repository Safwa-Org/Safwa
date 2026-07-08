package com.tasneem.safwa.features.home.domain.repository

import com.tasneem.safwa.features.core.domain.model.Product

interface HomeRepository {
    suspend fun getProducts(first: Int, after: String? = null, sortKey: String? = null, reverse: Boolean? = null): List<Product>
}

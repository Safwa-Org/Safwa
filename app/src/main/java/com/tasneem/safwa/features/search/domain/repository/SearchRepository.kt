package com.tasneem.safwa.features.search.domain.repository

import com.tasneem.safwa.features.core.domain.model.Product

interface SearchRepository {
    suspend fun searchProducts(query: String, first: Int = 20): List<Product>
}

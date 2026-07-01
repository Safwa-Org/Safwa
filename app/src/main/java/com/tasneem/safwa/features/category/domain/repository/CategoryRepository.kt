package com.tasneem.safwa.features.category.domain.repository

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(first: Int = 100): List<Category>
    suspend fun getProductsByCategory(category: String, first: Int = 20): List<Product>
}


package com.tasneem.safwa.features.category.di

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.features.category.data.repository.CategoryRepositoryImpl
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {
    @Provides
    @Singleton
    fun provideCategoryRepository(
        remoteDataSource: ProductRemoteDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(remoteDataSource)
    }
}


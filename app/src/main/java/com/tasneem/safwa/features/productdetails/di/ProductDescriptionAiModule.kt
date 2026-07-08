package com.tasneem.safwa.features.productdetails.di

import com.tasneem.safwa.features.productdetails.data.datasource.ProductDescriptionAiRemoteDataSource
import com.tasneem.safwa.features.productdetails.data.datasource.ProductDescriptionAiRemoteDataSourceImpl
import com.tasneem.safwa.features.productdetails.data.repository.ProductDescriptionAiRepositoryImpl
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDescriptionAiRepository
import com.tasneem.safwa.features.productdetails.domain.usecase.GenerateProductDescriptionUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDescriptionAiModule {

    @Binds
    abstract fun bindProductDescriptionAiRemoteDataSource(
        impl: ProductDescriptionAiRemoteDataSourceImpl
    ): ProductDescriptionAiRemoteDataSource

    @Binds
    abstract fun bindProductDescriptionAiRepository(
        impl: ProductDescriptionAiRepositoryImpl
    ): ProductDescriptionAiRepository

    companion object {
        @Provides
        fun provideGenerateProductDescriptionUseCase(
            repository: ProductDescriptionAiRepository
        ): GenerateProductDescriptionUseCase = GenerateProductDescriptionUseCase(repository)
    }
}
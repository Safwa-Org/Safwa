package com.tasneem.safwa.features.productdetails.di

import com.tasneem.safwa.features.productdetails.data.repository.ProductDetailsRepositoryImpl
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDetailsRepository
import com.tasneem.safwa.features.productdetails.domain.usecase.GetProductDetailsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDetailsModule {

    @Binds
    abstract fun bindProductDetailsRepository(
        impl: ProductDetailsRepositoryImpl
    ): ProductDetailsRepository

    companion object {
        @Provides
        fun provideDetailsUseCase(
            detailsRepository: ProductDetailsRepository,
        ): GetProductDetailsUseCase =
            GetProductDetailsUseCase(detailsRepository)
    }
}

package com.tasneem.safwa.features.home.di

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.features.home.data.repository.HomeRepositoryImpl
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    @Singleton
    fun provideHomeRepository(
        remoteDataSource: ProductRemoteDataSource
    ): HomeRepository {
        return HomeRepositoryImpl(remoteDataSource)
    }
}

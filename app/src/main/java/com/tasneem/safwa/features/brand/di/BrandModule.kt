package com.tasneem.safwa.features.brand.di

import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.safwa.features.brand.data.repository.BrandRepositoryImpl
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository

@Module
@InstallIn(SingletonComponent::class)
object BrandModule {
    @Provides
    @Singleton
    fun provideBrandRepository(
        remoteDataSource: BrandRemoteDataSource,
        sessionPreferencesRepository: SessionPreferencesRepository
    ): BrandRepository {
        return BrandRepositoryImpl(remoteDataSource, sessionPreferencesRepository)
    }
}

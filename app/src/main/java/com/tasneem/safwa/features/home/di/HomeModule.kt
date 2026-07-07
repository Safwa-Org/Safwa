package com.tasneem.safwa.features.home.di

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.features.home.data.datasource.AiRemoteDataSource
import com.tasneem.safwa.features.home.data.datasource.AiRemoteDataSourceImpl
import com.tasneem.safwa.features.home.data.repository.AiRepositoryImpl
import com.tasneem.safwa.features.home.data.repository.HomeRepositoryImpl
import com.tasneem.safwa.features.home.domain.repository.AiRepository
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

    @Provides
    @Singleton
    fun provideAiRemoteDataSource(): AiRemoteDataSource {
        return AiRemoteDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideAiRepository(
        aiRemoteDataSource: AiRemoteDataSource
    ): AiRepository {
        return AiRepositoryImpl(aiRemoteDataSource)
    }
}

package com.tasneem.safwa.features.aichat.di

import com.tasneem.safwa.core.ai.domain.ChatBotAiService
import com.tasneem.safwa.features.aichat.data.datasource.AiSearchRemoteDataSource
import com.tasneem.safwa.features.aichat.data.datasource.AiSearchRemoteDataSourceImpl
import com.tasneem.safwa.features.aichat.data.repository.AiSearchRepositoryImpl
import com.tasneem.safwa.features.aichat.domain.repository.AiSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiSearchModule {

    @Provides
    @Singleton
    fun provideAiSearchRemoteDataSource(
        chatBotAiService: ChatBotAiService
    ): AiSearchRemoteDataSource = AiSearchRemoteDataSourceImpl(chatBotAiService)

    @Provides
    @Singleton
    fun provideAiSearchRepository(
        dataSource: AiSearchRemoteDataSource
    ): AiSearchRepository = AiSearchRepositoryImpl(dataSource)
}

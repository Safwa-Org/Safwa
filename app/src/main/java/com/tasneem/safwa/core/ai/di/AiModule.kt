package com.tasneem.safwa.core.ai.di

import com.tasneem.safwa.core.ai.data.GenerativeAiServiceImpl
import com.tasneem.safwa.core.ai.domain.GenerativeAiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Provides
    @Singleton
    fun provideGenerativeAiService(): GenerativeAiService {
        return GenerativeAiServiceImpl()
    }
}

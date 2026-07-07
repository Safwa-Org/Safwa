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

    @Provides
    @Singleton
    fun provideOpenRouterApi(okHttpClient: okhttp3.OkHttpClient): com.tasneem.safwa.core.ai.data.remote.OpenRouterApi {
        return retrofit2.Retrofit.Builder()
            .baseUrl("https://openrouter.ai/")
            .client(okHttpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(com.tasneem.safwa.core.ai.data.remote.OpenRouterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatBotAiService(api: com.tasneem.safwa.core.ai.data.remote.OpenRouterApi): com.tasneem.safwa.core.ai.domain.ChatBotAiService {
        return com.tasneem.safwa.core.ai.data.ChatBotAiServiceImpl(api)
    }
}

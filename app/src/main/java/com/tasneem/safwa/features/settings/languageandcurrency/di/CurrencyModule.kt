package com.tasneem.safwa.features.settings.languageandcurrency.di

import com.tasneem.network.interceptor.CurrencyProvider
import com.tasneem.safwa.features.settings.languageandcurrency.data.CurrencyRateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyProvider(
        impl: CurrencyRateManager
    ): CurrencyProvider
}
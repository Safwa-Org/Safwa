package com.tasneem.safwa.features.settings.languageandcurrency.di

import com.tasneem.safwa.features.settings.languageandcurrency.data.repository.ExchangeRateRepositoryImpl
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.ExchangeRateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExchangeRateModule {

    @Binds
    @ViewModelScoped
    abstract fun bindExchangeRateRepository(
        impl: ExchangeRateRepositoryImpl
    ): ExchangeRateRepository
}
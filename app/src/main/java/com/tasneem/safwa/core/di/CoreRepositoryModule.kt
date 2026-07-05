package com.tasneem.safwa.core.di

import com.tasneem.safwa.core.data.repository.RemoteUserRepositoryImpl
import com.tasneem.safwa.core.data.repository.SessionPreferencesRepositoryImpl
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.NetworkStatusProvider
import com.tasneem.safwa.core.util.NetworkStatusProviderImpl
import com.tasneem.safwa.features.settings.languageandcurrency.data.repository.CurrencyRepositoryImpl
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSessionPreferencesRepository(
        sessionPreferencesRepositoryImpl: SessionPreferencesRepositoryImpl
    ): SessionPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindRemoteUserRepository(
        impl: RemoteUserRepositoryImpl
    ): RemoteUserRepository

    @Binds
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindNetworkStatusProvider(
        impl: NetworkStatusProviderImpl
    ): NetworkStatusProvider
}
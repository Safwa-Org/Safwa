package com.tasneem.safwa.features.settings.savedaddresses.di

import com.tasneem.safwa.features.settings.savedaddresses.data.repository.AddressLookupRepositoryImpl
import com.tasneem.safwa.features.settings.savedaddresses.data.repository.CountryRepositoryImpl
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressLookupRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.CountryRepository
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
    abstract fun bindAddressLookupRepository(
        impl: AddressLookupRepositoryImpl
    ): AddressLookupRepository

    @Binds
    @Singleton
    abstract fun bindCountryRepository(
        impl: CountryRepositoryImpl
    ): CountryRepository
}
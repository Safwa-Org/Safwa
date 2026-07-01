package com.tasneem.safwa.core.di

import com.tasneem.safwa.core.data.repository.SessionPreferencesRepositoryImpl
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
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
}
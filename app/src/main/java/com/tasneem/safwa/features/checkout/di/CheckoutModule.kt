package com.tasneem.safwa.features.checkout.di

import com.tasneem.safwa.features.checkout.data.repository.CheckoutRepositoryImpl
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckoutModule {

    @Binds
    @Singleton
    abstract fun bindCheckoutRepository(
        impl: CheckoutRepositoryImpl
    ): CheckoutRepository
}

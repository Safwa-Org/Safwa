package com.tasneem.safwa.features.cart.di

import com.tasneem.safwa.features.cart.data.repository.CartRepositoryImpl
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    @javax.inject.Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository
}

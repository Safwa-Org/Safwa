package com.tasneem.safwa.features.wishlist.di

import android.content.Context
import com.tasneem.safwa.core.dp.SafwaDB
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistDao
import com.tasneem.safwa.features.wishlist.data.repository.WishlistRepositoryImpl
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WishlistProvideModule {
    @Provides
    @Singleton
    fun provideWishlistDao(@ApplicationContext context: Context): WishlistDao {
        return SafwaDB.getDatabase(context).wishlistDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WishlistBindModule {

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(
        wishlistRepositoryImpl: WishlistRepositoryImpl
    ): WishlistRepository
}
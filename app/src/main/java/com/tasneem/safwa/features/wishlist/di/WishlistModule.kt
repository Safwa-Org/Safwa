package com.tasneem.safwa.features.wishlist.di

import android.content.Context
import com.tasneem.safwa.features.wishlist.data.dp.SafwaDP
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
        return SafwaDP.getDatabase(context).wishlistDao()
    }
}
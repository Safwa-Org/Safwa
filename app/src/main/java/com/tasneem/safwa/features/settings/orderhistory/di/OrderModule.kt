package com.tasneem.safwa.features.settings.orderhistory.di

import com.tasneem.safwa.core.dp.SafwaDB
import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.OrderDao
import com.tasneem.safwa.features.settings.orderhistory.data.repository.OrderRepositoryImpl
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    companion object {
        @dagger.Provides
        @Singleton
        fun provideOrderDao(
            @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
        ): OrderDao {
            return SafwaDB.getDatabase(context).orderDao()
        }
    }
}

package com.tasneem.safwa.features.payment.di

import com.tasneem.safwa.features.payment.data.repository.PaymentRepositoryImpl
import com.tasneem.safwa.features.payment.data.source.PaymentRemoteDataSource
import com.tasneem.safwa.features.payment.data.source.PaymentRemoteDataSourceImpl
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRemoteDataSource(
        paymentRemoteDataSourceImpl: PaymentRemoteDataSourceImpl
    ): PaymentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
}

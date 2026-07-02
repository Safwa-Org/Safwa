package com.tasneem.safwa.features.payment.di

import com.tasneem.safwa.features.payment.data.source.ShopifyDepositApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentNetworkModule {

    @Provides
    @Singleton
    fun provideShopifyDepositApi(): ShopifyDepositApi {
        return Retrofit.Builder()
            .baseUrl("https://elb.deposit.shopifycs.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopifyDepositApi::class.java)
    }

}

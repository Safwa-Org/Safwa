package com.tasneem.network.di

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.network.datasource.cart.CartRemoteDataSourceImpl
import com.tasneem.network.datasource.order.OrderRemoteDataSource
import com.tasneem.network.datasource.order.OrderRemoteDataSourceImpl
import com.tasneem.network.datasource.currency.ExchangeRateApi
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSource
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSourceImpl
import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.datasource.product.ProductRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.PaymentRemoteDataSource
import com.tasneem.network.datasource.payment.PaymentRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.ShopifyDepositApi
import com.tasneem.safwa.network.BuildConfig
import com.tasneem.network.datasource.auth.AuthRemoteDataSource
import com.tasneem.network.datasource.auth.AuthRemoteDataSourceImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient =
        ApolloClient.Builder()
            .serverUrl(BuildConfig.SHOPIFY_ENDPOINT)
            .addHttpHeader("X-Shopify-Storefront-Access-Token", BuildConfig.STOREFRONT_TOKEN)
            .build()

    @Provides
    @Singleton
    fun provideShopifyDepositApi(): ShopifyDepositApi {
        return Retrofit.Builder()
            .baseUrl("https://elb.deposit.shopifycs.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopifyDepositApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateApi(): ExchangeRateApi {
        return Retrofit.Builder()
            .baseUrl("https://api.exchangerate-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    abstract fun bindProductRemoteDatasource(
        impl: ProductRemoteDataSourceImpl
    ): ProductRemoteDataSource
    
    @Binds
    abstract fun bindCartRemoteDataSource(
        impl: CartRemoteDataSourceImpl
    ): CartRemoteDataSource

    @Binds
    abstract fun bindPaymentRemoteDatasource(
        impl: PaymentRemoteDataSourceImpl
    ): PaymentRemoteDataSource

    @Binds
    abstract fun bindOrderRemoteDataSource(
        impl: OrderRemoteDataSourceImpl
    ): OrderRemoteDataSource
    abstract fun bindExchangeRateRemoteDataSource(
        impl: ExchangeRateRemoteDataSourceImpl
    ): ExchangeRateRemoteDataSource

}
package com.tasneem.network.di

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.network.datasource.brand.BrandRemoteDataSourceImpl
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
import com.tasneem.network.datasource.auth.AuthRemoteDataSource
import com.tasneem.network.datasource.auth.AuthRemoteDataSourceImpl
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSource
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSourceImpl
import com.tasneem.network.datasource.location.CountryApi
import com.tasneem.network.datasource.location.CountryRemoteDataSource
import com.tasneem.network.datasource.location.CountryRemoteDataSourceImpl
import com.tasneem.network.datasource.location.LocationIqApi
import com.tasneem.safwa.network.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
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
            .baseUrl(BuildConfig.Currency_Exchange_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationIqApi(): LocationIqApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.Location_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationIqApi::class.java)

    @Provides
    @Singleton
    @Named("restCountriesClient")
    fun provideRestCountriesOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val authedRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.RESTCOUNTRIES_API_KEY}")
                    .build()
                chain.proceed(authedRequest)
            }
            .build()

    @Provides
    @Singleton
    fun provideCountryApi(@Named("restCountriesClient") client: OkHttpClient): CountryApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.Countries_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountryApi::class.java)
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
    abstract fun bindBrandRemoteDatasource(
        impl: BrandRemoteDataSourceImpl
    ): BrandRemoteDataSource
    
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
    @Binds
    abstract fun bindExchangeRateRemoteDataSource(
        impl: ExchangeRateRemoteDataSourceImpl
    ): ExchangeRateRemoteDataSource

    @Binds
    abstract fun bindAddressValidationRemoteDataSource(
        impl: AddressValidationRemoteDataSourceImpl
    ): AddressValidationRemoteDataSource

    @Binds
    abstract fun bindCountryRemoteDataSource(
        impl: CountryRemoteDataSourceImpl
    ): CountryRemoteDataSource

}
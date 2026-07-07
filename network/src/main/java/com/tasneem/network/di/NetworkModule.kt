package com.tasneem.network.di

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.network.datasource.brand.BrandRemoteDataSourceImpl
import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.network.datasource.payment.IPayMockRemoteDataSource
import com.tasneem.network.datasource.payment.PayMockRemoteDataSource
import com.tasneem.network.datasource.cart.CartRemoteDataSourceImpl
import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSource
import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSourceImpl
import com.tasneem.network.datasource.checkout.ShopifyAdminApi
import com.tasneem.network.datasource.order.OrderRemoteDataSource
import com.tasneem.network.datasource.order.OrderRemoteDataSourceImpl
import com.tasneem.network.datasource.currency.ExchangeRateApi
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSource
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSourceImpl
import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.datasource.product.ProductRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.PaymentRemoteDataSource
import com.tasneem.network.datasource.payment.PaymentRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.PayMockApiService
import com.tasneem.network.datasource.payment.ShopifyDepositApi
import okhttp3.logging.HttpLoggingInterceptor
import com.tasneem.network.datasource.auth.AuthRemoteDataSource
import com.tasneem.network.datasource.auth.AuthRemoteDataSourceImpl
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSource
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSourceImpl
import com.tasneem.network.datasource.location.LocationIqApi
import com.tasneem.safwa.network.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.url.toString().contains("api/v1/payments")) {
                    val jsonString = """{"id": "mock_id_123", "status": "approved"}"""
                    Response.Builder()
                        .code(200)
                        .message("OK")
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .body(jsonString.toResponseBody("application/json".toMediaTypeOrNull()))
                        .addHeader("content-type", "application/json")
                        .build()
                } else {
                    chain.proceed(request)
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient =
        ApolloClient.Builder()
            .serverUrl(BuildConfig.SHOPIFY_ENDPOINT)
            .addHttpHeader("X-Shopify-Storefront-Access-Token", BuildConfig.STOREFRONT_TOKEN)
            .build()

    @Provides
    @Singleton
    fun provideShopifyDepositApi(okHttpClient: OkHttpClient): ShopifyDepositApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SHOPIFY_DEPOSIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopifyDepositApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateApi(okHttpClient: OkHttpClient): ExchangeRateApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.Currency_Exchange_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationIqApi(okHttpClient: OkHttpClient): LocationIqApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.Location_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationIqApi::class.java)


    @Provides
    @Singleton
    fun provideShopifyAdminApi(okHttpClient: OkHttpClient): ShopifyAdminApi {
        val adminClient = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader(
                            "X-Shopify-Access-Token",
                            BuildConfig.SHOPIFY_ADMIN_API_ACCESS_TOKEN
                        )
                        .build()
                )
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SHOPIFY_ADMIN_BASE_URL)
            .client(adminClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopifyAdminApi::class.java)
    }

    @Provides
    @Singleton
    fun providePayMockApiService(okHttpClient: OkHttpClient): PayMockApiService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.29:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayMockApiService::class.java)
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
    abstract fun bindPayMockRemoteDataSource(
        impl: PayMockRemoteDataSource
    ): IPayMockRemoteDataSource

    @Binds
    abstract fun bindCheckoutRemoteDataSource(
        impl: CheckoutRemoteDataSourceImpl
    ): CheckoutRemoteDataSource

}
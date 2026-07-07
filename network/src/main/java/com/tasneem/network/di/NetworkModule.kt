package com.tasneem.network.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.DefaultHttpEngine
import com.apollographql.cache.normalized.api.DefaultCacheKeyGenerator
import com.apollographql.cache.normalized.api.DefaultCacheResolver
import com.apollographql.cache.normalized.normalizedCache
import com.apollographql.cache.normalized.sql.SqlNormalizedCacheFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.tasneem.network.datasource.address.CustomerAddressRemoteDataSource
import com.tasneem.network.datasource.address.CustomerAddressRemoteDataSourceImpl
import com.tasneem.network.datasource.auth.AuthRemoteDataSource
import com.tasneem.network.datasource.auth.AuthRemoteDataSourceImpl
import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.network.datasource.brand.BrandRemoteDataSourceImpl
import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.network.datasource.cart.CartRemoteDataSourceImpl
import com.tasneem.network.datasource.checkout.AdminProxyApi
import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSource
import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSourceImpl
import com.tasneem.network.datasource.currency.ExchangeRateApi
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSource
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSourceImpl
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSource
import com.tasneem.network.datasource.location.AddressValidationRemoteDataSourceImpl
import com.tasneem.network.datasource.location.LocationIqApi
import com.tasneem.network.datasource.order.OrderRemoteDataSource
import com.tasneem.network.datasource.order.OrderRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.IPayMockRemoteDataSource
import com.tasneem.network.datasource.payment.PayMockApiService
import com.tasneem.network.datasource.payment.PayMockRemoteDataSource
import com.tasneem.network.datasource.payment.PaymentRemoteDataSource
import com.tasneem.network.datasource.payment.PaymentRemoteDataSourceImpl
import com.tasneem.network.datasource.payment.ShopifyDepositApi
import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.datasource.product.ProductRemoteDataSourceImpl
import com.tasneem.network.interceptor.PriceConversionInterceptor
import com.tasneem.safwa.network.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ---------------------------------------------------------------
    // Base client: logging + payment mock interceptor. NO price
    // conversion here. This is what every Retrofit service should use.
    // ---------------------------------------------------------------
    @Provides
    @Singleton
    @BasicOkHttpClient
    fun provideBaseOkHttpClient(): OkHttpClient {
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
    fun provideShopifyOkHttpClient(
        @BasicOkHttpClient baseClient: OkHttpClient,
        priceConversionInterceptor: PriceConversionInterceptor
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(priceConversionInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(
        @ApplicationContext context: Context,
         okHttpClient: OkHttpClient
    ): ApolloClient {
        val sqliteFactory = SqlNormalizedCacheFactory(context, "apollo.db")
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.SHOPIFY_ENDPOINT)
            .addHttpHeader("X-Shopify-Storefront-Access-Token", BuildConfig.STOREFRONT_TOKEN)
            .httpEngine(DefaultHttpEngine(okHttpClient))
            .normalizedCache(
                normalizedCacheFactory = sqliteFactory,
                cacheKeyGenerator = DefaultCacheKeyGenerator,
                cacheResolver = DefaultCacheResolver
            )
            .build()
    }


    @Provides
    @Singleton
    fun provideShopifyDepositApi(
        @BasicOkHttpClient okHttpClient: OkHttpClient
    ): ShopifyDepositApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SHOPIFY_DEPOSIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopifyDepositApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateApi(
        @BasicOkHttpClient okHttpClient: OkHttpClient
    ): ExchangeRateApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.Currency_Exchange_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationIqApi(
        @BasicOkHttpClient okHttpClient: OkHttpClient
    ): LocationIqApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.Location_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationIqApi::class.java)


    @Provides
    @Singleton
    fun provideFirebaseAppCheck(): FirebaseAppCheck = FirebaseAppCheck.getInstance()

    @Provides
    @Singleton
    fun provideAdminProxyApi(okHttpClient: OkHttpClient): AdminProxyApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.ADMIN_PROXY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdminProxyApi::class.java)

    @Provides
    @Singleton
    fun providePayMockApiService(
        @BasicOkHttpClient okHttpClient: OkHttpClient
    ): PayMockApiService {
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
    abstract fun bindCustomerAddressRemoteDataSource(
        impl: CustomerAddressRemoteDataSourceImpl
    ): CustomerAddressRemoteDataSource

    @Binds
    abstract fun bindCheckoutRemoteDataSource(
        impl: CheckoutRemoteDataSourceImpl
    ): CheckoutRemoteDataSource

}
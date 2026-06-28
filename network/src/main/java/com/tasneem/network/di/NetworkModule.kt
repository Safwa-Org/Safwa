package com.tasneem.network.di

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.datasource.product.ProductRemoteDataSourceImpl
import com.tasneem.safwa.network.BuildConfig
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
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindProductRemoteDatasource(
        impl: ProductRemoteDataSourceImpl
    ): ProductRemoteDataSource
}
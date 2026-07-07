package com.tasneem.safwa.features.reviews.di

import com.tasneem.safwa.features.reviews.data.datasource.ReviewRemoteDataSource
import com.tasneem.safwa.features.reviews.data.datasource.ReviewRemoteDataSourceImpl
import com.tasneem.safwa.features.reviews.data.repository.ReviewRepositoryImpl
import com.tasneem.safwa.features.reviews.domain.repository.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReviewModule {
    @Binds
    @Singleton
    abstract fun bindReviewRemoteDataSource(impl: ReviewRemoteDataSourceImpl): ReviewRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository
}
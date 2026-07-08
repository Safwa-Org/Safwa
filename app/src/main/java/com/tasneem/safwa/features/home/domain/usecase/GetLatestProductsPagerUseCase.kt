package com.tasneem.safwa.features.home.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.data.paging.LatestProductsPagingSource
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestProductsPagerUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(pageSize: Int = 8): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { LatestProductsPagingSource(repository) }
        ).flow
    }
}

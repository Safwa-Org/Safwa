package com.tasneem.safwa.features.home.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.repository.HomeRepository

class LatestProductsPagingSource(
    private val repository: HomeRepository
) : PagingSource<String, Product>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Product> {
        val cursor = params.key
        return try {
            val response = repository.getPaginatedProducts(
                first = params.loadSize,
                after = cursor,
                sortKey = "CREATED_AT",
                reverse = true
            )
            LoadResult.Page(
                data = response.items,
                prevKey = null,
                nextKey = if (response.hasNextPage) response.endCursor else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Product>): String? {
        return null
    }
}

package com.tasneem.safwa.features.productdetails.domain.usecase

import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductDetailsRepository
) {
    suspend operator fun invoke(handle: String): ProductDetails {
        return repository.getProductDetailsByHandle(handle)
    }
}
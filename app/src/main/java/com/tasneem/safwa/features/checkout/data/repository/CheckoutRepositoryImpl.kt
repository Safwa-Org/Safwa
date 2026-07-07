package com.tasneem.safwa.features.checkout.data.repository

import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSource
import com.tasneem.network.exception.AppException
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.NoInternetException
import com.tasneem.network.exception.UserErrorException
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.data.mapper.toDomain
import com.tasneem.safwa.features.checkout.data.mapper.toDto
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class CheckoutRepositoryImpl @Inject constructor(
    private val remoteDataSource: CheckoutRemoteDataSource
) : CheckoutRepository {

    override suspend fun createDraftOrder(request: DraftOrderRequest): Resource<DraftOrder> =
        runCatchingResource("Failed to create the order") {
            remoteDataSource.createDraftOrder(request.toDto()).toDomain()
        }

    override suspend fun completeDraftOrder(draftOrderId: String): Resource<CompletedOrder> =
        runCatchingResource("Failed to create the order") {
            remoteDataSource.completeDraftOrder(
                draftOrderId = draftOrderId,
                paymentPending = true
            ).toDomain() ?: throw EmptyResponseException()
        }

    override suspend fun markOrderAsPaid(orderId: String): Resource<Unit> =
        runCatchingResource("Failed to confirm the payment") {
            remoteDataSource.markOrderAsPaid(orderId)
        }

    override suspend fun cancelOrder(orderId: String): Resource<Unit> =
        runCatchingResource("Failed to cancel the order") {
            remoteDataSource.cancelOrder(orderId, notifyCustomer = false)
        }

    private inline fun <T> runCatchingResource(
        fallbackMessage: String,
        block: () -> T
    ): Resource<T> = try {
        Resource.Success(block())
    } catch (e: AppException) {
        Resource.Error(e.toUserMessage(fallbackMessage), e)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: fallbackMessage, e)
    }

    private fun AppException.toUserMessage(fallback: String): String = when (this) {
        is NoInternetException -> "No internet connection"
        is NetworkException -> "Network error, please try again"
        is UserErrorException -> message
        is GraphQlException -> errors.ifEmpty { fallback }
        else -> fallback
    }
}

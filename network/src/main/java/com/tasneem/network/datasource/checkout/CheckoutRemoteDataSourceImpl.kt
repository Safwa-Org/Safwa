package com.tasneem.network.datasource.checkout

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderInputDto
import com.tasneem.network.dto.checkout.GraphQLResponseDto
import com.tasneem.network.dto.checkout.UserErrorDto
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.UnauthenticatedException
import com.tasneem.network.exception.UnknownException
import com.tasneem.network.exception.UserErrorException
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

class CheckoutRemoteDataSourceImpl @Inject constructor(
    private val adminProxyApi: AdminProxyApi,
    private val firebaseAuth: FirebaseAuth,
    private val appCheck: FirebaseAppCheck
) : CheckoutRemoteDataSource {

    override suspend fun createDraftOrder(input: DraftOrderInputDto): DraftOrderDto {
        val response = safeAdminCall {
            adminProxyApi.draftOrderCreate(
                authorizationHeader(),
                appCheckHeader(),
                mapOf("input" to input)
            )
        }
        val payload = response.dataOrThrow().draftOrderCreate
            ?: throw EmptyResponseException()
        payload.userErrors.throwIfNotEmpty()
        return payload.draftOrder ?: throw EmptyResponseException()
    }

    override suspend fun completeDraftOrder(
        draftOrderId: String,
        paymentPending: Boolean
    ): CompletedDraftOrderDto {
        val response = safeAdminCall {
            adminProxyApi.draftOrderComplete(
                authorizationHeader(),
                appCheckHeader(),
                mapOf("id" to draftOrderId, "paymentPending" to paymentPending)
            )
        }
        val payload = response.dataOrThrow().draftOrderComplete
            ?: throw EmptyResponseException()
        payload.userErrors.throwIfNotEmpty()
        return payload.draftOrder ?: throw EmptyResponseException()
    }

    override suspend fun markOrderAsPaid(orderId: String) {
        val response = safeAdminCall {
            adminProxyApi.orderMarkAsPaid(
                authorizationHeader(),
                appCheckHeader(),
                mapOf("id" to orderId)
            )
        }
        val payload = response.dataOrThrow().orderMarkAsPaid
            ?: throw EmptyResponseException()
        payload.userErrors.throwIfNotEmpty()
    }

    override suspend fun cancelOrder(orderId: String, notifyCustomer: Boolean) {
        val response = safeAdminCall {
            adminProxyApi.orderCancel(
                authorizationHeader(),
                appCheckHeader(),
                mapOf("orderId" to orderId, "notifyCustomer" to notifyCustomer)
            )
        }
        val payload = response.dataOrThrow().orderCancel
            ?: throw EmptyResponseException()
        (payload.orderCancelUserErrors + payload.userErrors).throwIfNotEmpty()
    }

    private suspend fun authorizationHeader(): String {
        val user = firebaseAuth.currentUser ?: throw UnauthenticatedException()
        val idToken = user.getIdToken(false).await().token ?: throw UnauthenticatedException()
        return "Bearer $idToken"
    }

    private suspend fun appCheckHeader(): String =
        appCheck.getAppCheckToken(false).await().token

    private suspend fun <T> safeAdminCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: UnauthenticatedException) {
            android.util.Log.e("CheckoutAdminProxy", "Not signed in / no ID token", e)
            throw e
        } catch (e: HttpException) {
            android.util.Log.e(
                "CheckoutAdminProxy",
                "HTTP ${e.code()} from admin proxy: ${e.response()?.errorBody()?.string()}",
                e
            )
            throw NetworkException()
        } catch (e: Exception) {
            android.util.Log.e("CheckoutAdminProxy", "Admin proxy call failed: ${e::class.java.simpleName}", e)
            throw UnknownException(e)
        }
    }

    private fun <T> GraphQLResponseDto<T>.dataOrThrow(): T {
        errors?.takeIf { it.isNotEmpty() }?.let { graphQlErrors ->
            throw GraphQlException(graphQlErrors.joinToString { it.message })
        }
        return data ?: throw EmptyResponseException()
    }

    private fun List<UserErrorDto>.throwIfNotEmpty() {
        if (isNotEmpty()) throw UserErrorException(map { it.message })
    }
}

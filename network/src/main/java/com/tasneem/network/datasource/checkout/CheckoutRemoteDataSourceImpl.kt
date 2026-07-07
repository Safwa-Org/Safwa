package com.tasneem.network.datasource.checkout

import com.tasneem.network.dto.checkout.AdminGraphQLRequest
import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderInputDto
import com.tasneem.network.dto.checkout.GraphQLResponseDto
import com.tasneem.network.dto.checkout.UserErrorDto
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.UserErrorException
import com.tasneem.network.exception.safeRestApiCall
import javax.inject.Inject

class CheckoutRemoteDataSourceImpl @Inject constructor(
    private val adminApi: ShopifyAdminApi
) : CheckoutRemoteDataSource {

    override suspend fun createDraftOrder(input: DraftOrderInputDto): DraftOrderDto {
        val response = safeRestApiCall {
            adminApi.draftOrderCreate(
                AdminGraphQLRequest(
                    query = DRAFT_ORDER_CREATE,
                    variables = mapOf("input" to input)
                )
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
        val response = safeRestApiCall {
            adminApi.draftOrderComplete(
                AdminGraphQLRequest(
                    query = DRAFT_ORDER_COMPLETE,
                    variables = mapOf(
                        "id" to draftOrderId,
                        "paymentPending" to paymentPending
                    )
                )
            )
        }
        val payload = response.dataOrThrow().draftOrderComplete
            ?: throw EmptyResponseException()
        payload.userErrors.throwIfNotEmpty()
        return payload.draftOrder ?: throw EmptyResponseException()
    }

    override suspend fun markOrderAsPaid(orderId: String) {
        val response = safeRestApiCall {
            adminApi.orderMarkAsPaid(
                AdminGraphQLRequest(
                    query = ORDER_MARK_AS_PAID,
                    variables = mapOf("input" to mapOf("id" to orderId))
                )
            )
        }
        val payload = response.dataOrThrow().orderMarkAsPaid
            ?: throw EmptyResponseException()
        payload.userErrors.throwIfNotEmpty()
    }

    override suspend fun cancelOrder(orderId: String, notifyCustomer: Boolean) {
        val response = safeRestApiCall {
            adminApi.orderCancel(
                AdminGraphQLRequest(
                    query = ORDER_CANCEL,
                    variables = mapOf(
                        "orderId" to orderId,
                        "notifyCustomer" to notifyCustomer,
                        "refund" to true,
                        "restock" to true,
                        "reason" to "CUSTOMER"
                    )
                )
            )
        }
        val payload = response.dataOrThrow().orderCancel
            ?: throw EmptyResponseException()
        (payload.orderCancelUserErrors + payload.userErrors).throwIfNotEmpty()
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

    private companion object {

        val DRAFT_ORDER_CREATE = $$"""
            mutation draftOrderCreate($input: DraftOrderInput!) {
              draftOrderCreate(input: $input) {
                draftOrder {
                  id
                  invoiceUrl
                  subtotalPriceSet { shopMoney { amount currencyCode } }
                  totalTaxSet { shopMoney { amount currencyCode } }
                  totalShippingPriceSet { shopMoney { amount currencyCode } }
                  totalPriceSet { shopMoney { amount currencyCode } }
                }
                userErrors { field message }
              }
            }
        """.trimIndent()

        val DRAFT_ORDER_COMPLETE = $$"""
            mutation draftOrderComplete($id: ID!, $paymentPending: Boolean) {
              draftOrderComplete(id: $id, paymentPending: $paymentPending) {
                draftOrder {
                  id
                  order { id name }
                }
                userErrors { field message }
              }
            }
        """.trimIndent()

        val ORDER_MARK_AS_PAID = $$"""
            mutation orderMarkAsPaid($input: OrderMarkAsPaidInput!) {
              orderMarkAsPaid(input: $input) {
                order { id name }
                userErrors { field message }
              }
            }
        """.trimIndent()

        val ORDER_CANCEL = $$"""
            mutation orderCancel(
              $orderId: ID!,
              $notifyCustomer: Boolean,
              $refund: Boolean!,
              $restock: Boolean!,
              $reason: OrderCancelReason!
            ) {
              orderCancel(
                orderId: $orderId,
                notifyCustomer: $notifyCustomer,
                refund: $refund,
                restock: $restock,
                reason: $reason
              ) {
                job { id done }
                orderCancelUserErrors { field message }
                userErrors { field message }
              }
            }
        """.trimIndent()
    }
}

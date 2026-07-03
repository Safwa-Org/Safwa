package com.tasneem.network.datasource.cart
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.tasneem.network.dto.ApplyDiscountResultDto
import com.tasneem.network.dto.CartDto
import com.tasneem.network.dto.DiscountCodeDto
import com.tasneem.network.exception.safeApiCall
import com.tasneem.safwa.network.AddToCartMutation
import com.tasneem.safwa.network.ApplyDiscountCodeMutation
import com.tasneem.safwa.network.CreateCartMutation
import com.tasneem.safwa.network.GetCartQuery
import com.tasneem.safwa.network.RemoveFromCartMutation
import com.tasneem.safwa.network.UpdateCartLineMutation
import com.tasneem.safwa.network.type.CartInput
import com.tasneem.safwa.network.type.CartLineInput
import com.tasneem.safwa.network.type.CartLineUpdateInput
import javax.inject.Inject

class CartRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : CartRemoteDataSource {

    override suspend fun createCart(variantId: String, quantity: Int): String {
        return safeApiCall(
            apiCall = {
                val input = CartInput(
                    lines = Optional.present(
                        listOf(
                            CartLineInput(
                                merchandiseId = variantId,
                                quantity = Optional.present(quantity)
                            )
                        )
                    )
                )
                apolloClient.mutation(CreateCartMutation(input = input)).execute()
            },
            mapper = { data ->
                val cart = data.cartCreate?.cart
                val userErrors = data.cartCreate?.userErrors
                
                if (!userErrors.isNullOrEmpty()) {
                    throw Exception(userErrors.first().message)
                }
                
                cart?.id ?: throw Exception("Failed to create cart, ID is null")
            }
        )
    }

    override suspend fun addToCart(cartId: String, variantId: String, quantity: Int) {
        safeApiCall(
            apiCall = {
                val lines = listOf(
                    CartLineInput(
                        merchandiseId = variantId,
                        quantity = Optional.present(quantity)
                    )
                )
                apolloClient.mutation(AddToCartMutation(cartId, lines)).execute()
            },
            mapper = { data ->
                val userErrors = data.cartLinesAdd?.userErrors
                
                if (!userErrors.isNullOrEmpty()) {
                    throw Exception(userErrors.first().message)
                }
            }
        )
    }

    override suspend fun getCart(cartId: String): CartDto {
        return safeApiCall(
            apiCall = {
                apolloClient.query(GetCartQuery(cartId)).execute()
            },
            mapper = { data ->
                val cart = data.cart
                if (cart != null) {
                    com.tasneem.network.mapper.CartMapper().map(cart)
                } else {
                    throw Exception("Cart not found")
                }
            }
        )
    }

    override suspend fun removeFromCart(cartId: String, lineIds: List<String>): String {
        return safeApiCall(
            apiCall = {
                apolloClient.mutation(RemoveFromCartMutation(cartId, lineIds)).execute()
            },
            mapper = { data ->
                val userErrors = data.cartLinesRemove?.userErrors
                if (!userErrors.isNullOrEmpty()) {
                    throw Exception(userErrors.first().message)
                }
                data.cartLinesRemove?.cart?.cost?.totalAmount?.amount?.toString() ?: "0.0"
            }
        )
    }

    override suspend fun updateCartLine(cartId: String, lineId: String, quantity: Int): String {
        return safeApiCall(
            apiCall = {
                val lines = listOf(
                    CartLineUpdateInput(
                        id = lineId,
                        quantity = Optional.present(quantity)
                    )
                )
                apolloClient.mutation(UpdateCartLineMutation(cartId, lines)).execute()
            },
            mapper = { data ->
                val userErrors = data.cartLinesUpdate?.userErrors
                if (!userErrors.isNullOrEmpty()) {
                    throw Exception(userErrors.first().message)
                }
                data.cartLinesUpdate?.cart?.cost?.totalAmount?.amount?.toString() ?: "0.0"
            }
        )
    }

    override suspend fun applyDiscountCode(cartId: String, discountCodes: List<String>): ApplyDiscountResultDto {
        return safeApiCall(
            apiCall = {
                apolloClient.mutation(ApplyDiscountCodeMutation(cartId, discountCodes)).execute()
            },
            mapper = { data ->
                val userErrors = data.cartDiscountCodesUpdate?.userErrors
                if (!userErrors.isNullOrEmpty()) {
                    throw Exception(userErrors.first().message)
                }

                val cart = data.cartDiscountCodesUpdate?.cart
                    ?: throw Exception("Failed to apply discount code")

                ApplyDiscountResultDto(
                    discountCodes = cart.discountCodes.map { discount ->
                        DiscountCodeDto(
                            code = discount.code,
                            applicable = discount.applicable
                        )
                    },
                    subtotalAmount = cart.cost.subtotalAmount.amount.toString(),
                    totalAmount = cart.cost.totalAmount.amount.toString(),
                    currency = cart.cost.totalAmount.currencyCode.name
                )
            }
        )
    }
}

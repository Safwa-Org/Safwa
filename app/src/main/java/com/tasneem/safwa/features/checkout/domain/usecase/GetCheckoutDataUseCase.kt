package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.checkout.domain.model.CheckoutData
import com.tasneem.safwa.features.payment.domain.usecase.GetSavedCardsUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressesUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCheckoutDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val getAddresses: GetAddressesUseCase,
    private val getSavedCards: GetSavedCardsUseCase
) {
    suspend operator fun invoke(): Resource<CheckoutData> {
        val userResource = authRepository.getCurrentUser()
        val user = (userResource as? Resource.Success)?.data
            ?: return Resource.Error("User not logged in or not found")

        if (user.cartId.isEmpty()) {
            return Resource.Error("Cart is empty")
        }

        val cart = when (val cartResource = cartRepository.getCart(user.cartId)) {
            is Resource.Success -> cartResource.data
            is Resource.Error -> return Resource.Error(cartResource.message, cartResource.throwable)
            is Resource.Loading -> return Resource.Error("Cart is still loading")
        }

        val token = user.customerAccessToken
        val addresses = if (token.isNullOrEmpty()) {
            emptyList()
        } else {
            getAddresses(token).getOrElse { emptyList() }
        }

        return Resource.Success(
            CheckoutData(
                cart = cart,
                addresses = addresses,
                savedCards = getSavedCards().first(),
                customerEmail = user.email
            )
        )
    }
}

package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(variantId: String, quantity: Int = 1): Resource<Unit> {
        val userResource = authRepository.getCurrentUser()
        
        if (userResource is Resource.Success) {
            val user = userResource.data
            if (user != null) {
                if (user.cartId.isEmpty()) {
                    val cartResult = cartRepository.createCart(variantId, quantity)
                    if (cartResult is Resource.Success) {
                        val newCartId = cartResult.data
                        authRepository.updateCartId(newCartId)
                        return Resource.Success(Unit)
                    } else {
                        val errorMessage = (cartResult as? Resource.Error)?.message ?: "Failed to create cart"
                        return Resource.Error(errorMessage)
                    }
                } else {
                    return cartRepository.addToCart(user.cartId, variantId, quantity)
                }
            }
        }
        
        return Resource.Error("Failed to add to cart: User not found or not logged in.")
    }
}

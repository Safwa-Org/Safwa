package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(lineId: String, quantity: Int): Resource<String> {
        val userResource = authRepository.getCurrentUser()
        
        if (userResource is Resource.Success) {
            val user = userResource.data
            if (user != null) {
                if (user.cartId.isEmpty()) {
                    return Resource.Error("Cart is empty")
                } else {
                    return cartRepository.removeFromCart(user.cartId, listOf(lineId), quantity)
                }
            }
        }
        
        return Resource.Error("User not logged in or not found")
    }
}

package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Resource<Cart> {
        val userResource = authRepository.getCurrentUser()
        
        if (userResource is Resource.Success) {
            val user = userResource.data
            if (user != null) {
                return if (user.cartId.isEmpty()) {
                    Resource.Error("Cart is empty")
                } else {
                    cartRepository.getCart(user.cartId)
                }
            }
        }
        return Resource.Error("User not logged in or not found")
    }
}

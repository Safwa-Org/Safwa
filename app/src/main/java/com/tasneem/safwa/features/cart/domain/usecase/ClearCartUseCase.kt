package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        val result = authRepository.updateCartId("")
        if (result is Resource.Success) {
            cartRepository.clearCartLocal()
        }
        return result
    }
}

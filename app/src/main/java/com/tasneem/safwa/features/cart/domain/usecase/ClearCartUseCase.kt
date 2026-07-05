package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.updateCartId("")
    }
}

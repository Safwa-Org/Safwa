package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import com.tasneem.safwa.features.auth.domain.session.AuthSessionManager
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.core.domain.usecase.ClearWishlistUseCase
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val clearWishlistUseCase: ClearWishlistUseCase,
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            clearWishlistUseCase()
            orderRepository.clearOrders()
            cartRepository.clearCartLocal()

            authSessionManager.signOutToGuest()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Logout failed")
        }
    }
}

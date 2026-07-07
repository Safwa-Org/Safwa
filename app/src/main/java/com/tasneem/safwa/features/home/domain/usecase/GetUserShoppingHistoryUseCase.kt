package com.tasneem.safwa.features.home.domain.usecase

import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.home.domain.model.UserShoppingHistory
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetUserShoppingHistoryUseCase @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) {
    suspend operator fun invoke(): UserShoppingHistory {
        val user = sessionPreferencesRepository.currentUser.firstOrNull()
        
        val wishlistItems = wishlistRepository.getWishlist().firstOrNull()?.map { it.title } ?: emptyList()
        
        val cartItems = try {
            if (user != null && user.cartId.isNotEmpty()) {
                val cartResource = cartRepository.getCart(user.cartId)
                if (cartResource is Resource.Success) {
                    cartResource.data.lines.map { it.productTitle }
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        val orderItems = try {
            if (user != null && !user.customerAccessToken.isNullOrEmpty()) {
                val ordersResourceFlow = orderRepository.getOrders(user.customerAccessToken)
                val ordersResource = ordersResourceFlow.firstOrNull()
                if (ordersResource is Resource.Success) {
                    ordersResource.data.flatMap { order ->
                        order.lineItems.map { it.title }
                    }
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return UserShoppingHistory(
            wishlistItems = wishlistItems.take(10),
            cartItems = cartItems.take(10),
            orderItems = orderItems.take(10)
        )
    }
}

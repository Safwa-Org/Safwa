package com.tasneem.safwa.features.home.domain.usecase

import com.tasneem.safwa.features.home.domain.repository.AiRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAiRecommendationsUseCase @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val homeRepository: HomeRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    private val aiRepository: AiRepository
) {
    operator fun invoke(): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading)
            
            // 1. Get User
            val user = sessionPreferencesRepository.currentUser.firstOrNull()
            
            // 2. Get Wishlist
            val wishlistItems = wishlistRepository.getWishlist().firstOrNull() ?: emptyList()
            
            // 3. Get Cart
            val cartItems = try {
                if (user != null && user.cartId.isNotEmpty()) {
                    val cartResource = cartRepository.getCart(user.cartId)
                    if (cartResource is Resource.Success) {
                        cartResource.data.lines.map { line -> 
                            Product(id = line.productId, title = line.productTitle)
                        }
                    } else emptyList()
                } else emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            // 4. Get Orders
            val orderItems = try {
                if (user != null && !user.customerAccessToken.isNullOrEmpty()) {
                    val ordersResourceFlow = orderRepository.getOrders(user.customerAccessToken)
                    val ordersResource = ordersResourceFlow.firstOrNull()
                    if (ordersResource is Resource.Success) {
                        ordersResource.data.flatMap { order ->
                            order.lineItems.map { lineItem ->
                                Product(id = "", title = lineItem.title) // approximate product
                            }
                        }
                    } else emptyList()
                } else emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            // 5. Get Shop Products
            val shopProducts = homeRepository.getProducts(first = 50)
            if (shopProducts.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@flow
            }
            
            // 6. Build Prompt
            val prompt = buildString {
                append("You are an AI shopping assistant for the 'Safwa' store.\n")
                append("The user has the following preferences based on their history:\n")
                if (wishlistItems.isNotEmpty()) {
                    append("- Wishlist: ${wishlistItems.joinToString { it.title }}\n")
                }
                if (cartItems.isNotEmpty()) {
                    append("- Cart: ${cartItems.joinToString { it.title }}\n")
                }
                if (orderItems.isNotEmpty()) {
                    append("- Past Orders: ${orderItems.joinToString { it.title }}\n")
                }
                if (wishlistItems.isEmpty() && cartItems.isEmpty() && orderItems.isEmpty()) {
                    append("- (No previous history, please recommend the most popular or diverse items)\n")
                }
                
                append("\nHere is the list of available shop products:\n")
                shopProducts.forEach { product ->
                    append("- [ID: ${product.id}] ${product.title} (Type: ${product.productType})\n")
                }
                
                append("\nBased on the user's preferences, recommend up to 5 products from the available shop products. ")
                append("Return ONLY the product IDs as a comma-separated list, with no other text, markdown, or explanation. ")
                append("For example: ID1,ID2,ID3")
            }
            
            // 7. Call Gemini AI via Repository
            val recommendedIds = aiRepository.getRecommendedProductIds(prompt)
            
            val recommendedProducts = shopProducts.filter { product ->
                recommendedIds.any { recommendedId -> 
                    product.id.contains(recommendedId, ignoreCase = true) || recommendedId.contains(product.id, ignoreCase = true)
                }
            }.take(5)
            
            emit(Resource.Success(recommendedProducts))
            
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get AI recommendations"))
        }
    }
}

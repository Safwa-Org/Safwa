package com.tasneem.safwa.features.wishlist.data.datasource.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.tasneem.safwa.features.core.domain.model.Product
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WishlistRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val wishlistsCollection = firestore.collection("wishlists")

    suspend fun getWishlist(userId: String): List<Product> {
        return try {
            val snapshot = wishlistsCollection.document(userId).collection("products").get().await()
            snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProduct(userId: String, product: Product) {
        try {
            val docId = product.id.replace("/", "_")
            wishlistsCollection.document(userId)
                .collection("products")
                .document(docId)
                .set(product)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun removeProduct(userId: String, productId: String) {
        try {
            val docId = productId.replace("/", "_")
            wishlistsCollection.document(userId)
                .collection("products")
                .document(docId)
                .delete()
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

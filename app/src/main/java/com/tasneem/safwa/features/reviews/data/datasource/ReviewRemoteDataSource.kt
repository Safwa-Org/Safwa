package com.tasneem.safwa.features.reviews.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.tasneem.safwa.features.reviews.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ReviewRemoteDataSource {
    fun observeReviews(productId: String): Flow<List<ReviewEntity>>
    suspend fun submitReview(review: ReviewEntity)
}

class ReviewRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRemoteDataSource {

    private fun itemsCollection(productId: String) =
        firestore.collection("reviews")
            .document(productId.replace("/", "_"))
            .collection("items")

    override fun observeReviews(productId: String): Flow<List<ReviewEntity>> {
        return itemsCollection(productId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toObject<ReviewEntity>() } }
    }

    override suspend fun submitReview(review: ReviewEntity) {
        itemsCollection(review.productId)
            .document(review.userId)
            .set(review)
            .await()
    }
}
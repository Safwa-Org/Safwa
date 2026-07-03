package com.tasneem.safwa.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.tasneem.safwa.core.data.mapper.toDomain
import com.tasneem.safwa.core.data.mapper.toEntity
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class RemoteUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteUserRepository {

    override fun observeUser(userId: String): Flow<User?> {
        return firestore.collection("users")
            .document(userId)
            .snapshots()
            .map { snapshot ->
                // CRITICAL: If it has pending writes, the change came from US locally.
                // Notice the () added to hasPendingWrites()
                if (snapshot.exists() && !snapshot.metadata.hasPendingWrites()) {
                    snapshot.toObject<UserEntity>()?.toDomain()
                } else {
                    null
                }
            }
    }

    override suspend fun uploadUser(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(user.toEntity())
            .await()
    }
}
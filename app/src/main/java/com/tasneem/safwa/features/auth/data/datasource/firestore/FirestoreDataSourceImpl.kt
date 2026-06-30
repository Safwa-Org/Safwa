package com.tasneem.safwa.features.auth.data.datasource.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.tasneem.safwa.core.data.model.UserEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreDataSource {

    override suspend fun saveUser(userEntity: UserEntity) {
        firestore.collection("users").document(userEntity.id).set(userEntity).await()
    }

    override suspend fun getUser(uid: String): UserEntity? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(UserEntity::class.java)
    }

    override suspend fun updateUser(uid: String, data: Map<String, Any>) {
        firestore.collection("users").document(uid).update(data).await()
    }
}
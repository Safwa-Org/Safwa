package com.tasneem.safwa.features.auth.data.datasource.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.tasneem.safwa.features.auth.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreDataSource {

    override suspend fun saveUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    override suspend fun getUser(uid: String): User? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    override suspend fun updateUser(uid: String, data: Map<String, Any>) {
        firestore.collection("users").document(uid).update(data).await()
    }

}
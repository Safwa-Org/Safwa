package com.tasneem.safwa.features.auth.data.datasource.firestore

import com.tasneem.safwa.features.auth.domain.model.User

interface FirestoreDataSource {
    suspend fun saveUser(user: User)
    suspend fun getUser(uid: String): User?
    suspend fun updateUser(uid: String, data: Map<String, Any>)
}
package com.tasneem.safwa.features.auth.data.datasource.firestore


import com.tasneem.safwa.core.data.model.UserEntity

interface FirestoreDataSource {
    suspend fun saveUser(userEntity: UserEntity)
    suspend fun getUser(uid: String): UserEntity?
    suspend fun updateUser(uid: String, data: Map<String, Any>)
}
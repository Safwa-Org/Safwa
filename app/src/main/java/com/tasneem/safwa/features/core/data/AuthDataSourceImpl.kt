package com.tasneem.safwa.features.core.data

import com.google.firebase.auth.FirebaseAuth
import com.tasneem.safwa.features.core.domain.AuthDataSource
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthDataSource {
    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}

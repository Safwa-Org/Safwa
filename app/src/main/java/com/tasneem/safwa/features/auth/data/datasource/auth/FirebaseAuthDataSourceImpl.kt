package com.tasneem.safwa.features.auth.data.datasource.auth

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : FirebaseAuthDataSource {
    override suspend fun signUpWithEmail(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()

    override suspend fun signInWithEmail(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password).await()

    override suspend fun signInWithGoogle(idToken: String) =
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()

    override suspend fun signInAnonymously() =
        firebaseAuth.signInAnonymously().await()

    override suspend fun signOut() {
        firebaseAuth.signOut()
        try {
            googleSignInClient.signOut()
            // Optional: googleSignInClient.revokeAccess() to completely disconnect the app
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email

    override suspend fun sendEmailVerification() {
        val user = firebaseAuth.currentUser ?: throw Exception("No user signed in")
        user.sendEmailVerification().await()
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}
package com.tasneem.safwa.features.auth.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tasneem.safwa.R
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSourceImpl
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSource
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSourceImpl
import com.tasneem.safwa.features.auth.data.repository.AuthRepositoryImpl
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GoogleLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GuestLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.LoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.LogoutUseCase
import com.tasneem.safwa.features.auth.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule  {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(
        @ApplicationContext context: Context
    ): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        options: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, options)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object AuthDataModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth): FirebaseAuthDataSource =
        FirebaseAuthDataSourceImpl(auth)

    @Provides
    @Singleton
    fun provideFirestoreDataSource(firestore: FirebaseFirestore): FirestoreDataSource =
        FirestoreDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource,
        firestoreDataSource: FirestoreDataSource
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthDataSource, firestoreDataSource)
}

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase =
        LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideGoogleLoginUseCase(repository: AuthRepository): GoogleLoginUseCase =
        GoogleLoginUseCase(repository)

    @Provides
    @Singleton
    fun provideGuestLoginUseCase(repository: AuthRepository): GuestLoginUseCase =
        GuestLoginUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase =
        LogoutUseCase(repository)
}
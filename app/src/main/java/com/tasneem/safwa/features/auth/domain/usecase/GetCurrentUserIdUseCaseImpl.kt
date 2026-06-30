package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.core.domain.usecase.GetCurrentUserIdUseCase
import javax.inject.Inject

class GetCurrentUserIdUseCaseImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : GetCurrentUserIdUseCase {
    override operator fun invoke(): String? {
        return authDataSource.getCurrentUserId()
    }
}

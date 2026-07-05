package com.tasneem.safwa.core.di

import com.tasneem.safwa.core.domain.usecase.preferences.GetAppPreferencesUseCase
import com.tasneem.safwa.core.domain.usecase.preferences.GetUserSessionUseCase
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.core.domain.usecase.preferences.SaveUserSessionUseCase
import com.tasneem.safwa.core.domain.usecase.preferences.UpdateAppPreferencesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PreferencesUseCaseModule {

    @Provides
    @ViewModelScoped
    fun providePreferencesUseCases(
        getUserSessionUseCase: GetUserSessionUseCase,
        saveUserSessionUseCase: SaveUserSessionUseCase,
        getAppPreferencesUseCase: GetAppPreferencesUseCase,
        updateAppPreferencesUseCase: UpdateAppPreferencesUseCase,
    ): PreferencesUseCases {
        return PreferencesUseCases(
            getUserSession = getUserSessionUseCase,
            saveUserSession = saveUserSessionUseCase,
            getAppPreferences = getAppPreferencesUseCase,
            updateAppPreferences = updateAppPreferencesUseCase,
        )
    }
}
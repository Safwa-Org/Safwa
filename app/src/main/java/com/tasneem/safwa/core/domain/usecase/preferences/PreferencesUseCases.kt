package com.tasneem.safwa.core.domain.usecase.preferences

data class PreferencesUseCases(
    val getUserSession: GetUserSessionUseCase,
    val saveUserSession: SaveUserSessionUseCase,
    val getAppPreferences: GetAppPreferencesUseCase,
    val updateAppPreferences: UpdateAppPreferencesUseCase,
    val getSavedAddresses: GetSavedAddressesUseCase,
    val updateAddress: UpdateAddressUseCase,
    val deleteAddress: DeleteAddressUseCase
)
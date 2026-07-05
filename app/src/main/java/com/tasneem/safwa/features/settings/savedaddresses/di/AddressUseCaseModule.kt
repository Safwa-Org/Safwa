package com.tasneem.safwa.features.settings.savedaddresses.di

import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressLookupRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.CountryRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.DeleteAddressUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressSuggestionsUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetCountriesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetSavedAddressesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.UpdateAddressUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
object AddressUseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetAddressSuggestionsUseCase(repo: AddressLookupRepository) =
        GetAddressSuggestionsUseCase(repo)

    @Provides
    @ViewModelScoped
    fun provideGetCountriesUseCase(repo: CountryRepository) =
        GetCountriesUseCase(repo)

    @Provides
    @ViewModelScoped
    fun provideGetSavedAddressesUseCase(repo: SessionPreferencesRepository) =
        GetSavedAddressesUseCase(repo)

    @Provides
    @ViewModelScoped
    fun provideDeleteAddressUseCase(localRepo: SessionPreferencesRepository,
                                    remoteRepo: RemoteUserRepository) =
        DeleteAddressUseCase(localRepo,remoteRepo)

    @Provides
    @ViewModelScoped
    fun provideUpdateAddressUseCase(localRepo: SessionPreferencesRepository,
                                    remoteRepo: RemoteUserRepository,
                                  @ApplicationScope  externalScope: CoroutineScope) =
        UpdateAddressUseCase(localRepo, remoteRepo, externalScope)

}
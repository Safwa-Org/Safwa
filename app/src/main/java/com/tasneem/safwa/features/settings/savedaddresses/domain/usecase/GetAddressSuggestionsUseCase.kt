package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressLookupRepository
import javax.inject.Inject

class GetAddressSuggestionsUseCase @Inject constructor(
    private val repo: AddressLookupRepository
) {
    suspend operator fun invoke(query: String, countryIso2: String): Result<List<AddressCandidate>> =
        repo.getSuggestions(query, countryIso2)
}
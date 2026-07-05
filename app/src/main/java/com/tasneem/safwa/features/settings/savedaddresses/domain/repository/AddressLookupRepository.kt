package com.tasneem.safwa.features.settings.savedaddresses.domain.repository

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate

interface AddressLookupRepository {
    suspend fun getSuggestions(query: String, countryIso2: String): Result<List<AddressCandidate>>
}
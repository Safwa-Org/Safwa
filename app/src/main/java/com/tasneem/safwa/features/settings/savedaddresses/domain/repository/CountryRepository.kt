package com.tasneem.safwa.features.settings.savedaddresses.domain.repository

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country

interface CountryRepository {
    suspend fun getCountries(): Result<List<Country>>
}
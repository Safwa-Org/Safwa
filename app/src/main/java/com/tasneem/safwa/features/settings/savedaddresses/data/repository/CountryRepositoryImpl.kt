package com.tasneem.safwa.features.settings.savedaddresses.data.repository

import com.tasneem.safwa.features.settings.savedaddresses.data.datasource.CountryLocalDataSource
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.CountryRepository
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val local: CountryLocalDataSource
) : CountryRepository {
    override suspend fun getCountries(): Result<List<Country>> = runCatching {
        local.getCountries()
            .map { Country(name = it.name, iso2 = it.iso2, iso3 = it.iso3) }
            .sortedBy { it.name }
    }
}
package com.tasneem.safwa.features.settings.savedaddresses.data.repository


import com.tasneem.network.datasource.location.CountryRemoteDataSource
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.CountryRepository
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val remote: CountryRemoteDataSource
) : CountryRepository {
    override suspend fun getCountries(): Result<List<Country>> = runCatching {
        remote.getCountries()
            .filter { it.codes?.alpha3 != null && it.names != null }
            .map {
                Country(
                    name = it.names!!.common,
                    iso2 = it.codes?.alpha2 ?: "",
                    iso3 = it.codes!!.alpha3!!
                )
            }
            .sortedBy { it.name }
    }
}
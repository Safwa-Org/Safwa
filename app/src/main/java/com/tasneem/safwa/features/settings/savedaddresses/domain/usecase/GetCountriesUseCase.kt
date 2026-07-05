package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.CountryRepository
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repo: CountryRepository
) {
    suspend operator fun invoke(): Result<List<Country>> = repo.getCountries()
}
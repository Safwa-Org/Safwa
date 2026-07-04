package com.tasneem.safwa.features.settings.savedaddresses.data.repository

import com.tasneem.network.datasource.location.AddressValidationRemoteDataSource
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressLookupRepository
import javax.inject.Inject

class AddressLookupRepositoryImpl @Inject constructor(
    private val remote: AddressValidationRemoteDataSource
) : AddressLookupRepository {

    override suspend fun getSuggestions(
        query: String,
        countryIso2: String
    ): Result<List<AddressCandidate>> = runCatching {
        if (query.isBlank()) return@runCatching emptyList()
        remote.getSuggestions(query, countryIso2).map { dto ->
            val addr = dto.address
            AddressCandidate(
                id = dto.placeId,
                displayLabel = dto.displayName,
                street = listOfNotNull(addr?.houseNumber, addr?.road)
                    .joinToString(" ")
                    .ifBlank { null },
                cityAndZip = listOfNotNull(addr?.city, addr?.postcode).joinToString(", "),
                countryCode = addr?.countryCode?.uppercase() ?: "",
                latitude = dto.lat.toDoubleOrNull() ?: 0.0,
                longitude = dto.lon.toDoubleOrNull() ?: 0.0
            )
        }
    }
}
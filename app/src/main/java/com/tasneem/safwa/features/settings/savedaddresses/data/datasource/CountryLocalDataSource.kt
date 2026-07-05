package com.tasneem.safwa.features.settings.savedaddresses.data.datasource


import android.content.Context
import com.tasneem.safwa.features.settings.savedaddresses.data.model.CountryAssetDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

interface CountryLocalDataSource {
    fun getCountries(): List<CountryAssetDto>
}

@Singleton
class CountryLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CountryLocalDataSource {
    private val cached: List<CountryAssetDto> by lazy {
        val json = context.assets.open("countries.json").bufferedReader().use { it.readText() }
        Json { ignoreUnknownKeys = true }.decodeFromString(json)
    }

    override fun getCountries(): List<CountryAssetDto> = cached
}
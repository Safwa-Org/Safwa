package com.tasneem.safwa.features.settings.savedaddresses.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tasneem.safwa.features.settings.savedaddresses.data.model.AddressCacheDto
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface AddressLocalDataSource {
    suspend fun getCachedAddresses(): List<AddressCacheDto>
    suspend fun cacheAddresses(addresses: List<AddressCacheDto>)
}

class AddressLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AddressLocalDataSource {

    private val key = stringPreferencesKey("cached_shopify_addresses_json")
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCachedAddresses(): List<AddressCacheDto> {
        val raw = dataStore.data.first()[key]
        android.util.Log.d("AddressCache", "READ raw value: $raw")
        if (raw == null) return emptyList()
        return try {
            json.decodeFromString(raw)
        } catch (e: Exception) {
            android.util.Log.d("AddressCache", "READ decode failed: ${e.message}")
            emptyList()
        }
    }

    override suspend fun cacheAddresses(addresses: List<AddressCacheDto>) {
        val encoded = json.encodeToString(addresses)
        android.util.Log.d("AddressCache", "WRITE: $encoded")
        dataStore.edit { it[key] = encoded }
    }
}
package com.tasneem.safwa.features.settings.languageandcurrency.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSource
import com.tasneem.network.interceptor.CurrencyProvider
import com.tasneem.safwa.core.data.source.local.PreferencesKeys
import com.tasneem.safwa.core.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRateManager @Inject constructor(
    private val remoteDataSource: ExchangeRateRemoteDataSource,
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val appScope: CoroutineScope
) : CurrencyProvider {
    companion object {
        const val STORE_BASE_CURRENCY = "USD"
        private const val RATE_TTL_MS = 24 * 60 * 60 * 1000L
    }

    @Volatile private var rates: Map<String, Double> = emptyMap()
    @Volatile private var displayCurrency: String = STORE_BASE_CURRENCY

    init {
        appScope.launch { bootstrap() }
    }

    private suspend fun bootstrap() {
        val prefs = dataStore.data.first()
        displayCurrency = prefs[PreferencesKeys.CURRENCY_CODE] ?: STORE_BASE_CURRENCY
        prefs[PreferencesKeys.RATES_JSON]?.let { json ->
            runCatching { Json.decodeFromString<Map<String, Double>>(json) }
                .onSuccess { rates = it }
        }

        appScope.launch {
            dataStore.data
                .map { it[PreferencesKeys.CURRENCY_CODE] ?: STORE_BASE_CURRENCY }
                .distinctUntilChanged()
                .collect { displayCurrency = it }
        }

        refreshIfStale()
    }

    suspend fun refreshIfStale(force: Boolean = false) {
        val prefs = dataStore.data.first()
        val lastFetch = prefs[PreferencesKeys.RATES_TIMESTAMP] ?: 0L
        val stale = System.currentTimeMillis() - lastFetch > RATE_TTL_MS
        if (!force && !stale && rates.isNotEmpty()) return

        runCatching { remoteDataSource.getLatestRates(STORE_BASE_CURRENCY) }
            .onSuccess { dto ->
                val fetched = dto.rates ?: return@onSuccess
                rates = fetched
                dataStore.edit {
                    it[PreferencesKeys.RATES_JSON] = Json.encodeToString(fetched)
                    it[PreferencesKeys.RATES_TIMESTAMP] = System.currentTimeMillis()
                }
            }
    }

    override fun currentDisplayCurrency(): String = displayCurrency

    override fun convertFromBase(amount: BigDecimal): BigDecimal {
        if (displayCurrency == STORE_BASE_CURRENCY) return amount
        val rate = rates[displayCurrency] ?: return amount
        return amount.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.HALF_UP)
    }

    override fun getStoreBaseCurrency(): String = STORE_BASE_CURRENCY
}
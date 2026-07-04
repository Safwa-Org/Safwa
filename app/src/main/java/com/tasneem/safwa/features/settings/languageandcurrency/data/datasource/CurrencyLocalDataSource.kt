package com.tasneem.safwa.features.settings.languageandcurrency.data.datasource

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import javax.inject.Inject

class CurrencyLocalDataSource @Inject constructor() {
    fun getLocalCurrencies(): List<Currency> {
        return listOf(
            Currency("EGP", "Egyptian Pound", "🇪🇬"),
            Currency("USD", "US Dollar", "🇺🇸"),
            Currency("EUR", "Euro", "🇪🇺"),
            Currency("GBP", "British Pound", "🇬🇧"),
            Currency("SAR", "Saudi Riyal", "🇸🇦"),
            Currency("AED", "UAE Dirham", "🇦🇪"),
            Currency("KWD", "Kuwaiti Dinar", "🇰🇼"),
            Currency("BHD", "Bahraini Dinar", "🇧🇭"),
            Currency("QAR", "Qatari Riyal", "🇶🇦"),
            Currency("OMR", "Omani Rial", "🇴🇲"),
            Currency("JOD", "Jordanian Dinar", "🇯🇴"),
            Currency("CAD", "Canadian Dollar", "🇨🇦"),
            Currency("AUD", "Australian Dollar", "🇦🇺"),
            Currency("JPY", "Japanese Yen", "🇯🇵"),
            Currency("CNY", "Chinese Yuan", "🇨🇳"),
            Currency("CHF", "Swiss Franc", "🇨🇭"),
            Currency("INR", "Indian Rupee", "🇮🇳"),
            Currency("TRY", "Turkish Lira", "🇹🇷"),
            Currency("RUB", "Russian Ruble", "🇷🇺"),
            Currency("ZAR", "South African Rand", "🇿🇦"),
            Currency("BRL", "Brazilian Real", "🇧🇷")
        )
    }
}
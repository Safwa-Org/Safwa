package com.tasneem.network.interceptor

import java.math.BigDecimal

interface CurrencyProvider {
    fun currentDisplayCurrency(): String
    fun convertFromBase(amount: BigDecimal): BigDecimal
    fun getStoreBaseCurrency(): String
}
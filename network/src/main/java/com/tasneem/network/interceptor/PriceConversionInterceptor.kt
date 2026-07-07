package com.tasneem.network.interceptor

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class PriceConversionInterceptor @Inject constructor(
    private val currencyProvider: CurrencyProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (currencyProvider.currentDisplayCurrency() == currencyProvider.getStoreBaseCurrency()) {
            return response
        }

        val body = response.body ?: return response
        val contentType = body.contentType()
        if (contentType?.subtype?.contains("json") != true) return response

        val rawJson = body.string()
        val convertedJson = runCatching { convert(rawJson) }.getOrDefault(rawJson)

        return response.newBuilder()
            .body(convertedJson.toResponseBody(contentType))
            .build()
    }

    private fun convert(json: String): String {
        val root = JsonParser.parseString(json)
        walk(root)
        return root.toString()
    }

    private fun walk(element: JsonElement) {
        when {
            element.isJsonObject -> {
                val obj = element.asJsonObject
                if (isMoneyNode(obj)) convertMoneyNode(obj)
                obj.entrySet().forEach { (_, value) -> walk(value) }
            }
            element.isJsonArray -> element.asJsonArray.forEach(::walk)
        }
    }

    private fun isMoneyNode(obj: JsonObject): Boolean {
        val amount = obj.get("amount")
        val currency = obj.get("currencyCode")
        return amount?.isJsonPrimitive == true && amount.asJsonPrimitive.isString &&
                currency?.isJsonPrimitive == true && currency.asJsonPrimitive.isString
    }

    private fun convertMoneyNode(obj: JsonObject) {
        val currency = obj.get("currencyCode").asString
        if (currency != currencyProvider.getStoreBaseCurrency()) return

        val amount = obj.get("amount").asString.toBigDecimalOrNull() ?: return
        val converted = currencyProvider.convertFromBase(amount)

        obj.addProperty("amount", converted.toPlainString())
        obj.addProperty("currencyCode", currencyProvider.currentDisplayCurrency())
    }
}
package com.tasneem.safwa.features.productdetails.data.datasource

import android.util.Log
import com.tasneem.safwa.core.ai.domain.ChatBotAiService
import javax.inject.Inject

class ProductDescriptionAiRemoteDataSourceImpl @Inject constructor(
    private val chatBotAiService: ChatBotAiService
) : ProductDescriptionAiRemoteDataSource {

    companion object {
        private const val TAG = "ProductDescriptionAI"
    }

    override suspend fun generateDescription(
        title: String,
        vendor: String,
        productType: String,
        price: String,
        currency: String,
        languageCode: String,
    ): String? {
        val languageName = resolveLanguageName(languageCode)

        val systemInstruction = """
            You are a tasteful e-commerce copywriter who fills in missing product descriptions.

            Write ONE short product description (2 to 4 sentences) for the item described by the user.

            Rules:
            - Write ONLY in $languageName. No other language, no transliteration.
            - Plain text only. No markdown, no quotation marks, no bullet points, no emojis.
            - Never mention that this was AI-generated, and never mention missing data.
            - Do not invent specific factual claims (materials, certifications, technical specs) that weren't given to you. You may describe look, feel, mood, and use-case in general terms.
            - The goal is to make the reader warm up to the product through appealing, sensory, aspirational language — NOT through direct sales pressure.
            - NEVER use direct calls to action or sales pressure language (e.g. "buy now", "don't miss out", "limited time", "act fast", "hurry").
            - NEVER use exclamation marks or superlative spam ("the best", "amazing", "incredible").
            - Keep tone aligned with the brand and category given (e.g. a watch brand reads more refined/understated than a casual accessory).
            - Output the description text only, nothing else.
        """.trimIndent()

        val prompt = buildString {
            appendLine("Product title: $title")
            if (vendor.isNotBlank()) appendLine("Brand: $vendor")
            if (productType.isNotBlank()) appendLine("Category: $productType")
            if (price.isNotBlank() && currency.isNotBlank()) appendLine("Price: $currency $price")
            append("\nWrite the description now.")
        }

        Log.d(TAG, "Requesting AI description for '$title' (lang=$languageCode) via OpenRouter")

        return try {
            val result = chatBotAiService.generateContent(
                prompt = prompt,
                systemInstruction = systemInstruction,
                responseMimeType = null,
                temperature = 0.7f
            ).trim()

            if (result.isBlank()) {
                null
            } else {
                result
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun resolveLanguageName(code: String): String = when (code.lowercase()) {
        "ar" -> "Arabic"
        "en" -> "English"
        else -> "English"
    }
}
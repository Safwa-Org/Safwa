package com.tasneem.safwa.features.home.data.datasource

import com.tasneem.safwa.core.ai.domain.GenerativeAiService
import com.tasneem.safwa.features.home.domain.model.AiSearchQuery
import com.tasneem.safwa.features.home.domain.model.UserShoppingHistory
import org.json.JSONObject
import javax.inject.Inject

class AiRemoteDataSourceImpl @Inject constructor(
    private val generativeAiService: GenerativeAiService
) : AiRemoteDataSource {

    override suspend fun getRecommendations(history: UserShoppingHistory): AiSearchQuery {
        val prompt = buildString {
            append("User History:\n")
            if (history.wishlistItems.isNotEmpty()) {
                append("- Wishlist: ${history.wishlistItems.joinToString()}\n")
            }
            if (history.cartItems.isNotEmpty()) {
                append("- Cart: ${history.cartItems.joinToString()}\n")
            }
            if (history.orderItems.isNotEmpty()) {
                append("- Past Orders: ${history.orderItems.joinToString()}\n")
            }
            if (history.wishlistItems.isEmpty() && history.cartItems.isEmpty() && history.orderItems.isEmpty()) {
                append("- (Empty)\n")
            }
            
            append("\nReturn JSON format exactly like this:\n")
            append("{\n  \"query\": \"extracted_keyword\",\n  \"first\": 20\n}")
        }

        val responseText = generativeAiService.generateContent(
            prompt = prompt,
            systemInstruction = "You are an analytical AI shopping assistant. Extract the most prominent noun or product category from the user's history and return it as a JSON search query. If the history is empty, or you cannot find a good keyword, return an empty string for the query.",
            responseMimeType = "application/json",
            temperature = 0.15f
        )
        
        return try {
            val jsonObject = JSONObject(responseText)
            val query = jsonObject.optString("query", "")
            val first = jsonObject.optInt("first", 20)
            AiSearchQuery(query = query, first = first)
        } catch (e: Exception) {
            AiSearchQuery(query = "", first = 20)
        }
    }
}

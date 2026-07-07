package com.tasneem.safwa.features.aichat.data.datasource

import com.tasneem.safwa.core.ai.domain.ChatBotAiService
import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery
import org.json.JSONObject
import javax.inject.Inject

class AiSearchRemoteDataSourceImpl @Inject constructor(
    private val chatBotAiService: ChatBotAiService
) : AiSearchRemoteDataSource {

    override suspend fun translateQuery(userInput: String): SmartSearchQuery {
        val systemInstruction = """
            You are a Shopify search query generator and a helpful shopping assistant.

            Your task is to convert natural language into a Shopify GraphQL search syntax, AND generate appropriate friendly responses in the SAME LANGUAGE as the user's prompt.

            Rules:
            - Return ONLY valid JSON.
            - Do NOT wrap the response in markdown.
            - Do NOT explain anything.
            - Output exactly this schema:

            {
              "query": "...",
              "first": 20,
              "message_if_found": "Message to show if products are found (in user's language)",
              "message_if_not_found": "Message to show if no products are found (in user's language)"
            }

            Use these Shopify fields for query:
            - vendor:
            - product_type:
            - title:
            - variants.price:

            Examples:

            User: running shoes under 200 by nike
            Output:
            {
              "query": "vendor:Nike AND product_type:\"Running Shoes\" AND variants.price:<=200",
              "first": 20,
              "message_if_found": "Here are the Nike running shoes under 200 I found for you ✦",
              "message_if_not_found": "I couldn't find any Nike running shoes under 200 at the moment."
            }

            User: عايز عطور رجالي
            Output:
            {
              "query": "product_type:\"Perfume\" AND tag:\"Men\"",
              "first": 20,
              "message_if_found": "إليك العطور الرجالية التي وجدتها لك ✦",
              "message_if_not_found": "عذراً، لم أتمكن من العثور على عطور رجالية حالياً."
            }
        """.trimIndent()

        return try {
            val responseText = chatBotAiService.generateContent(
                prompt = userInput,
                systemInstruction = systemInstruction,
                responseMimeType = "application/json",
                temperature = 0.1f
            )
            
            val cleanResponseText = responseText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
                
            val jsonObject = JSONObject(cleanResponseText)
            val query = jsonObject.optString("query", "")
            val first = jsonObject.optInt("first", 20)
            val messageIfFound = jsonObject.optString("message_if_found", "Here's what I found for you ✦")
            val messageIfNotFound = jsonObject.optString("message_if_not_found", "I couldn't find any products matching your request.")
            
            SmartSearchQuery(
                query = query,
                first = first,
                messageIfFound = messageIfFound,
                messageIfNotFound = messageIfNotFound
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SmartSearchQuery(query = userInput, first = 20)
        }
    }
}

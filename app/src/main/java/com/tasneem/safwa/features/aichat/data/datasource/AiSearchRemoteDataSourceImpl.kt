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
            You are a Shopify search query generator.

            Your task is to convert natural language into Shopify GraphQL search syntax.

            Rules:
            - Return ONLY valid JSON.
            - Do NOT wrap the response in markdown.
            - Do NOT explain anything.
            - Output exactly this schema:

            {
              "query": "...",
              "first": 20
            }

            Use these Shopify fields:
            - vendor:
            - product_type:
            - title:
            - variants.price:

            Examples:

            User: running shoes under 200 by nike
            Output:
            {
              "query": "vendor:Nike AND product_type:\"Running Shoes\" AND variants.price:<=200",
              "first": 20
            }

            User: black adidas shoes
            Output:
            {
              "query": "vendor:Adidas AND Black",
              "first": 20
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
            
            SmartSearchQuery(query = query, first = first)
        } catch (e: Exception) {
            e.printStackTrace()
            SmartSearchQuery(query = userInput, first = 20)
        }
    }
}

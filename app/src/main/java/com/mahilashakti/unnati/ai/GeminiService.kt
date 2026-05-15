package com.mahilashakti.unnati.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.mahilashakti.unnati.utils.Constants

class GeminiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        }
    )

    suspend fun generateResponse(prompt: String, context: String): String {
        val fullPrompt = content {
            text("System Instruction: You are the 'Mahila-Shakti Unnati' AI Financial Assistant. " +
                 "You help women in Self Help Groups manage their microfinance. " +
                 "Answer queries politely in English, Hindi, Kannada, or Tamil as requested. " +
                 "Here is the current SHG context: $context\n\nUser Question: $prompt")
        }

        return try {
            val response = generativeModel.generateContent(fullPrompt)
            response.text ?: "I'm sorry, I couldn't generate a response."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}. Please check your internet connection or API key."
        }
    }

    suspend fun predictLoanRisk(memberContext: String): String {
        val prompt = content {
            text("Analyze the following member financial data and return a JSON object with: " +
                 "'risk_level' (Low, Medium, High) and 'reasoning' (short explanation). " +
                 "Data: $memberContext")
        }

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Unable to assess risk."
        } catch (e: Exception) {
            "Error assessing risk: ${e.localizedMessage}"
        }
    }
}

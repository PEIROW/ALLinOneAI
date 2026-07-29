package com.example.dataandroidbot.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatRepository(private val context: Context) {

    private val prefs = PreferencesManager(context)

    var provider: AiProvider = AiProvider.OLLAMA
    var modelName: String = "phi3"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(history: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        val messagesArray = JSONArray()
        history.forEach { msg ->
            if (!msg.isLoading) {
                messagesArray.put(
                    JSONObject()
                        .put("role", msg.role)
                        .put("content", msg.content)
                )
            }
        }

        val bodyJson = JSONObject()
            .put("model", modelName)
            .put("messages", messagesArray)
            .put("stream", false)

        val requestBody = bodyJson.toString()
            .toRequestBody("application/json".toMediaType())

        val request = when (provider) {
            AiProvider.OLLAMA -> {
                Request.Builder()
                    .url("http://10.0.2.2:11434/v1/chat/completions")
                    .post(requestBody)
                    .build()
            }
            AiProvider.OPENAI -> {
                val key = prefs.openAiApiKey
                if (key.isBlank()) throw Exception("OpenAI API key is missing. Go to Settings.")
                Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $key")
                    .post(requestBody)
                    .build()
            }
            AiProvider.XAI -> {
                val key = prefs.xaiApiKey
                if (key.isBlank()) throw Exception("Grok (xAI) API key is missing. Go to Settings.")
                Request.Builder()
                    .url("https://api.x.ai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $key")
                    .post(requestBody)
                    .build()
            }
            AiProvider.GEMINI -> {
                val key = prefs.geminiApiKey
                if (key.isBlank()) throw Exception("Gemini API key is missing. Go to Settings.")
                Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions")
                    .addHeader("Authorization", "Bearer $key")
                    .post(requestBody)
                    .build()
            }
        }

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No error body"
                throw Exception("HTTP ${response.code}: $errorBody")
            }

            val responseBody = response.body?.string()
                ?: throw Exception("Empty response")

            val json = JSONObject(responseBody)
            json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
}

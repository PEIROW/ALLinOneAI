package com.example.dataandroidbot.data

data class ChatMessage(
    val role: String,          // "user", "assistant", or "system"
    val content: String,
    val isLoading: Boolean = false
)

enum class AiProvider {
    OLLAMA,
    OPENAI,
    XAI,
    GEMINI
}

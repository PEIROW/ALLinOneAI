package com.example.dataandroidbot.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataandroidbot.data.AiProvider
import com.example.dataandroidbot.data.ChatMessage
import com.example.dataandroidbot.data.ChatRepository
import com.example.dataandroidbot.data.PreferencesManager
import kotlinx.coroutines.launch

class ChatViewModel(context: Context) : ViewModel() {

    private val repository = ChatRepository(context)
    private val prefs = PreferencesManager(context)

    val messages = mutableStateListOf<ChatMessage>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    val currentProvider = mutableStateOf(AiProvider.OLLAMA)
    val currentModel = mutableStateOf("phi3")

    val isNsfwMode = mutableStateOf(false)
    val currentNsfwModel = mutableStateOf("dolphin-llama3")
    val nsfwSystemPrompt = mutableStateOf(PreferencesManager.DEFAULT_NSFW_PROMPT)

    val scrollToBottomEvent = mutableStateOf(false)
    val snackbarMessage = mutableStateOf<String?>(null)

    init {
        messages.add(
            ChatMessage(
                role = "assistant",
                content = "Hello! I'm ALLinOneAI.\n\nSupports Local (Ollama), ChatGPT, Grok, and Gemini.\nToggle NSFW Mode in the top bar for uncensored models."
            )
        )
        loadNsfwPreference()
    }

    fun loadNsfwPreference() {
        isNsfwMode.value = prefs.isNsfwMode
        currentNsfwModel.value = prefs.nsfwModel
        nsfwSystemPrompt.value = prefs.nsfwSystemPrompt
        if (isNsfwMode.value) applyNsfwMode()
    }

    fun setProvider(provider: AiProvider, model: String) {
        currentProvider.value = provider
        currentModel.value = model
        repository.provider = provider
        repository.modelName = model

        val name = when (provider) {
            AiProvider.OLLAMA -> "Local (Ollama)"
            AiProvider.OPENAI -> "ChatGPT"
            AiProvider.XAI -> "Grok"
            AiProvider.GEMINI -> "Gemini"
        }
        messages.add(ChatMessage("assistant", "Switched to $name → $model"))
    }

    fun toggleNsfwMode() {
        isNsfwMode.value = !isNsfwMode.value
        prefs.isNsfwMode = isNsfwMode.value

        if (isNsfwMode.value) {
            applyNsfwMode()
            messages.add(ChatMessage("assistant", "NSFW Mode enabled\nModel: ${currentNsfwModel.value}"))
        } else {
            setProvider(AiProvider.OLLAMA, "phi3")
            messages.add(ChatMessage("assistant", "NSFW Mode disabled"))
        }
    }

    private fun applyNsfwMode() {
        setProvider(AiProvider.OLLAMA, currentNsfwModel.value)
    }

    fun updateNsfwSettings(model: String, prompt: String) {
        currentNsfwModel.value = model
        nsfwSystemPrompt.value = prompt
        prefs.nsfwModel = model
        prefs.nsfwSystemPrompt = prompt
        if (isNsfwMode.value) applyNsfwMode()
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || isLoading.value) return

        messages.add(ChatMessage("user", text.trim()))
        messages.add(ChatMessage("assistant", "", isLoading = true))
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val historyToSend = mutableListOf<ChatMessage>()

                if (isNsfwMode.value) {
                    historyToSend.add(
                        ChatMessage("system", nsfwSystemPrompt.value)
                    )
                }

                historyToSend.addAll(messages.filter { !it.isLoading })

                val reply = repository.sendMessage(historyToSend)
                val lastIndex = messages.lastIndex
                messages[lastIndex] = ChatMessage("assistant", reply)
            } catch (e: Exception) {
                val lastIndex = messages.lastIndex
                messages[lastIndex] = ChatMessage("assistant", "Error: ${e.message ?: "Unknown error"}")
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun showSnackbarMessage(message: String) {
        snackbarMessage.value = message
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    fun clearScrollEvent() {
        scrollToBottomEvent.value = false
    }
}

package com.example.dataandroidbot.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier.modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dataandroidbot.data.PreferencesManager
import com.example.dataandroidbot.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    var openAiKey by remember { mutableStateOf(preferencesManager.openAiApiKey) }
    var xaiKey by remember { mutableStateOf(preferencesManager.xaiApiKey) }
    var geminiKey by remember { mutableStateOf(preferencesManager.geminiApiKey) }

    var showOpenAi by remember { mutableStateOf(false) }
    var showXai by remember { mutableStateOf(false) }
    var showGemini by remember { mutableStateOf(false) }

    var selectedNsfwModel by remember { mutableStateOf(preferencesManager.nsfwModel) }
    var promptText by remember { mutableStateOf(preferencesManager.nsfwSystemPrompt) }

    var savedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("API Keys", style = MaterialTheme.typography.titleMedium)
            Text("Keys are stored encrypted on this device only.", style = MaterialTheme.typography.bodySmall)

            OutlinedTextField(
                value = openAiKey,
                onValueChange = { openAiKey = it },
                label = { Text("OpenAI API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showOpenAi) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showOpenAi = !showOpenAi }) {
                        Text(if (showOpenAi) "Hide" else "Show")
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = xaiKey,
                onValueChange = { xaiKey = it },
                label = { Text("Grok (xAI) API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showXai) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showXai = !showXai }) {
                        Text(if (showXai) "Hide" else "Show")
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = geminiKey,
                onValueChange = { geminiKey = it },
                label = { Text("Gemini API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showGemini) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showGemini = !showGemini }) {
                        Text(if (showGemini) "Hide" else "Show")
                    }
                },
                singleLine = true
            )

            Button(
                onClick = {
                    preferencesManager.openAiApiKey = openAiKey.trim()
                    preferencesManager.xaiApiKey = xaiKey.trim()
                    preferencesManager.geminiApiKey = geminiKey.trim()
                    savedMessage = "API keys saved"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save API Keys")
            }

            HorizontalDivider()

            Text("NSFW Mode Settings", style = MaterialTheme.typography.titleMedium)

            Text("Preferred Uncensored Model")
            val nsfwModels = listOf("dolphin-llama3", "dolphin-mistral", "dolphin-mixtral", "hermes3")
            nsfwModels.forEach { model ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedNsfwModel = model }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedNsfwModel == model,
                        onClick = { selectedNsfwModel = model }
                    )
                    Text(model, modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("NSFW System Prompt") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                maxLines = 8
            )

            Button(
                onClick = {
                    viewModel.updateNsfwSettings(selectedNsfwModel, promptText.trim())
                    savedMessage = "NSFW settings saved"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save NSFW Settings")
            }

            savedMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

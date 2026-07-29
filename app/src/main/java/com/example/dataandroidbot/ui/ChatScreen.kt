package com.example.dataandroidbot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier.modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.dataandroidbot.data.AiProvider
import com.example.dataandroidbot.data.ChatMessage
import com.example.dataandroidbot.data.PreferencesManager
import com.example.dataandroidbot.util.BiometricHelper
import com.example.dataandroidbot.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    preferencesManager: PreferencesManager,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricHelper = remember { BiometricHelper(context) }

    var inputText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.lastIndex)
        }
    }

    LaunchedEffect(viewModel.scrollToBottomEvent.value) {
        if (viewModel.scrollToBottomEvent.value) {
            if (viewModel.messages.isNotEmpty()) {
                listState.animateScrollToItem(viewModel.messages.lastIndex)
            }
            viewModel.clearScrollEvent()
        }
    }

    LaunchedEffect(viewModel.snackbarMessage.value) {
        viewModel.snackbarMessage.value?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Long)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Long)
            errorMessage = null
        }
    }

    fun requestBiometricAndOpenSettings() {
        if (activity == null) {
            onOpenSettings()
            return
        }

        when (biometricHelper.getBiometricStatus()) {
            BiometricHelper.BiometricStatus.READY -> {
                biometricHelper.authenticate(
                    activity = activity,
                    onSuccess = { onOpenSettings() },
                    onError = { errorMessage = it },
                    onFailed = { errorMessage = "Authentication failed" },
                    onCancelled = {}
                )
            }
            BiometricHelper.BiometricStatus.NONE_ENROLLED -> {
                errorMessage = "No biometric enrolled. Please set one up in device Settings."
            }
            else -> onOpenSettings() // fallback
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ALLinOneAI")
                        Text(
                            text = when (viewModel.currentProvider.value) {
                                AiProvider.OLLAMA -> "Local • ${viewModel.currentModel.value}"
                                AiProvider.OPENAI -> "ChatGPT • ${viewModel.currentModel.value}"
                                AiProvider.XAI -> "Grok • ${viewModel.currentModel.value}"
                                AiProvider.GEMINI -> "Gemini • ${viewModel.currentModel.value}"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (viewModel.isNsfwMode.value) "NSFW" else "SFW",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (viewModel.isNsfwMode.value)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = viewModel.isNsfwMode.value,
                            onCheckedChange = { viewModel.toggleNsfwMode() },
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    IconButton(onClick = { requestBiometricAndOpenSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Models")
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        Text("Open Source (Ollama)", modifier = Modifier.padding(12.dp))
                        listOf("phi3", "llama3.2", "gemma2:2b").forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    viewModel.setProvider(AiProvider.OLLAMA, model)
                                    showMenu = false
                                }
                            )
                        }

                        Divider()
                        Text("Uncensored / NSFW", modifier = Modifier.padding(12.dp))
                        listOf("dolphin-llama3", "dolphin-mistral", "hermes3").forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    viewModel.setProvider(AiProvider.OLLAMA, model)
                                    showMenu = false
                                }
                            )
                        }

                        Divider()
                        Text("Cloud Providers", modifier = Modifier.padding(12.dp))
                        DropdownMenuItem(text = { Text("gpt-4o-mini") }, onClick = {
                            viewModel.setProvider(AiProvider.OPENAI, "gpt-4o-mini")
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text("grok-4.5") }, onClick = {
                            viewModel.setProvider(AiProvider.XAI, "grok-4.5")
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text("gemini-3.6-flash") }, onClick = {
                            viewModel.setProvider(AiProvider.GEMINI, "gemini-3.6-flash")
                            showMenu = false
                        })
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(viewModel.messages) { message ->
                    MessageBubble(message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask me anything...") },
                    maxLines = 4,
                    enabled = !viewModel.isLoading.value
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    enabled = inputText.isNotBlank() && !viewModel.isLoading.value
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val backgroundColor = if (isUser) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (message.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = textColor
                )
            } else {
                Text(text = message.content, color = textColor)
            }
        }
    }
}

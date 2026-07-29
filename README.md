# ALLinOneAI

**All-in-One Android AI Chatbot**

A modern Android chatbot built with Kotlin + Jetpack Compose that supports multiple AI providers:

- **Local open-source models** via Ollama (including uncensored models)
- **ChatGPT** (OpenAI)
- **Grok** (xAI)
- **Gemini** (Google)

### Features

- Multi-provider switching
- NSFW Mode with editable system prompt + selectable uncensored models
- Encrypted storage of API keys (Android Keystore + EncryptedSharedPreferences)
- Biometric lock (fingerprint / face) for Settings
- Notification channels + clickable deep links
- Runtime permission handling with rationale dialogs
- Clean Jetpack Compose UI

### Tech Stack

- Kotlin
- Jetpack Compose
- OkHttp
- AndroidX Security Crypto
- AndroidX Biometric
- Navigation Compose

### Getting Started

1. Clone the repository
2. Open in Android Studio
3. Add your API keys in Settings (or use local Ollama)
4. For local models: install [Ollama](https://ollama.com) and pull models (e.g. `ollama pull dolphin-llama3`)

### Project Status

This is an evolving starter project. Core architecture and major features are implemented.

---

Created with assistance from Grok.

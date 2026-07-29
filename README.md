# ALLinOneAI

**All-in-One Android AI Chatbot**

A modern Android chatbot built with **Kotlin + Jetpack Compose** that supports multiple AI providers in one app.

## Supported Providers

- **Local open-source models** via [Ollama](https://ollama.com) (including uncensored models)
- **ChatGPT** (OpenAI)
- **Grok** (xAI)
- **Gemini** (Google)

## Features

- Multi-provider switching from the menu
- **NSFW Mode** toggle with editable system prompt + selectable uncensored models
- Encrypted storage of API keys (Android Keystore + EncryptedSharedPreferences)
- Biometric lock (fingerprint / face) before opening Settings
- Notification channels + clickable deep links
- Clean Material 3 UI

## Project Status

The critical project structure issues have been resolved. The app now contains:

- Complete Gradle setup
- AndroidManifest + Application class
- MainActivity + Navigation
- ChatScreen + SettingsScreen
- ChatViewModel
- All previously written helpers (Repository, Preferences, Biometric, Notifications)

## How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/munster1987/ALLinOneAI.git
   ```

2. **Open in Android Studio** (Hedgehog or newer recommended)
   - File → Open → select the project folder
   - Let Gradle sync finish

3. **Create Launcher Icons** (required once)
   - Right-click `res` → New → Image Asset
   - Choose Launcher Icons (Adaptive and Legacy)
   - Generate default icons

4. **Run on Emulator or Device**
   - For local models: Install [Ollama](https://ollama.com) on your Windows machine and run:
     ```powershell
     ollama pull phi3
     ollama pull dolphin-llama3
     ollama serve
     ```
   - The emulator uses `10.0.2.2` to reach Ollama on the host machine.

5. **Add API Keys** (optional)
   - Open the app → Settings (gear icon, requires biometric)
   - Paste your OpenAI / xAI / Gemini keys

## Important Notes

- Minimum SDK: 26
- Cleartext traffic is allowed for local Ollama (`http://10.0.2.2`)
- On a physical device, change the Ollama URL in `ChatRepository.kt` from `10.0.2.2` to your computer’s local IP address.

## Package

`com.example.dataandroidbot`

---

Built with assistance from Grok.

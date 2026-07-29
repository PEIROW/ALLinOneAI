# ALLinOneAI

**All-in-One Android AI Chatbot**

A modern Android chatbot built with **Kotlin + Jetpack Compose** supporting multiple AI providers.

## Supported Providers

- **Local open-source models** via [Ollama](https://ollama.com) (including uncensored models)
- **ChatGPT** (OpenAI)
- **Grok** (xAI)
- **Gemini** (Google)

## Features

- Multi-provider switching
- NSFW Mode with editable system prompt + selectable uncensored models
- Encrypted API key storage (Android Keystore)
- Biometric lock for Settings
- Notification channels + deep links
- Material 3 UI

## Status

**Critical issues resolved.** The project should now open, sync, compile, and run in Android Studio.

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/munster1987/ALLinOneAI.git
   ```

2. Open the project in **Android Studio** (latest stable recommended).

3. Let Gradle sync finish. If prompted to generate/update the Gradle Wrapper, accept it.

4. Run on an emulator or device.

### Local Models (Ollama)

On your Windows machine:

```powershell
ollama pull phi3
ollama pull dolphin-llama3
ollama serve
```

The emulator reaches Ollama via `http://10.0.2.2:11434`.

### API Keys (Optional)

Open the app → tap the gear icon (Settings) → enter your OpenAI / xAI / Gemini keys.

## Notes

- Minimum SDK: 26
- Package: `com.example.dataandroidbot`
- On a **physical device**, change `10.0.2.2` in `ChatRepository.kt` to your computer’s local IP address.

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

Built with assistance from Grok.

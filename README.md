# Buffer: The State-Machine Mind Game

Buffer is a 1v1 tactical deduction game for Android. It combines the simultaneous-reveal mechanics of Rock-Paper-Scissors with deep, state-tracking constraints. Players must manage their physical inputs, predict bot behavior, and balance offense and defense under a strict memory-based cooldown system.

## The Core Concept
You and your opponent (the Bot) play two hands simultaneously:
* **The Right Hand (The State):** Throws 1 to 5 fingers. 
* **The Left Hand (The Read):** Guesses the opponent's Right Hand.
* **The Buffer (Cooldown):** When you play a number with your Right Hand, it enters your Buffer. **You cannot play that number again for the next two rounds.**

First to score 3 points wins. 

## Tech Stack & Architecture
This project is built using modern Android development standards to cleanly separate the complex state-machine rules from the UI.
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel)
* **State Management:** StateFlow & Coroutines

## Algorithmic Bot Engine
The opponent AI doesn't just guess randomly. It utilizes heuristic logic to track game states:
* **Offensive Parsing:** The bot analyzes your active `playerBuffer` and calculates your remaining legal moves, prioritizing its guesses based on your narrowed options.
* **Defensive Tracking:** The bot monitors your past successful reads and attempts to select Right Hand states that avoid your historical prediction patterns.

## Installation & Build Instructions
To build and run this game locally:
1. Clone this repository: `git clone https://github.com/yourusername/buffer-app.git`
2. Open the project in **Android Studio**.
3. Sync Gradle files.
4. Build the APK: Go to `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
5. Connect your Android device via USB (with Developer Options enabled) or use an emulator.
6. Install via ADB: `adb install app/build/outputs/apk/debug/app-debug.apk`

## Developer
**Aswin Salish** 
* Portfolio: [aswinsalish.me](https://aswinsalish.me)

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.

# BUFFER // Tactical State-Machine 

> *"A 1v1 psychological deduction game disguised as a mobile app."*

[![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/home.html)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-F58A27?style=for-the-badge)](https://developer.android.com/topic/architecture)

**Buffer** is a high-speed, simultaneous-reveal logic game built natively for Android. It takes the core concept of Rock-Paper-Scissors and completely shatters it by introducing a **2-Round Memory Cooldown** and a **Heuristic AI Taunt Engine**. 

You are not playing against a random number generator. You are playing against an algorithm that tracks your habits, analyzes your frequency, and actively trash-talks your blunders.

---

## The Core Mechanics (The State Machine)

Players control two distinct actions simultaneously:
* **The Right Hand (The State):** Throw 1 to 5 fingers to set your state.
* **The Left Hand (The Read):** Throw 1 to 5 fingers to guess your opponent's state.

### The "Buffer" (2-Round Cooldown)
When you play a number with your Right Hand, it enters your **Buffer**. **You cannot legally play that number again for the next two rounds.** This single rule forces the game to escalate from blind luck into algorithmic prediction. By Round 3, both players' available moves are constrained. You must manage your own legal options while reverse-engineering your opponent's remaining probability matrix. First to 3 points wins.

---

## The Engine: 3-Tier Heuristic AI

The Bot in this game does not cheat; it simply out-thinks you. The AI is built on a custom heuristic engine with three distinct personality tiers:

* **Level 1: The Grunt (Easy)** - Functionally blind. Plays completely randomly.
* **Level 2: The Sniper (Medium)** - Mathematically perfect. It tracks your current Buffer and will never make an illegal guess. It also actively avoids guessing your previous throw.
* **Level 3: The Grandmaster (Hard)** - Tracks your soul. The Grandmaster cross-references your current available moves against your **entire match history**. It uses frequency analysis to predict the number you lean on when panicked, and employs a 20% "Double-Bluff" override to intentionally play the numbers you think it's too smart to play. 

---

## The Psychological Taunt Engine

Inspired by the dynamic bots of Chess.com, Buffer features a zero-latency, offline **Taunt Manager**. 

The AI monitors the exact `RoundResult` state and reacts instantly:
* **The Blunder Trap:** If you waste your Read guessing a number that is currently locked in the Bot's Buffer (an impossible move), the engine detects the blunder and ruthlessly mocks your lack of working memory.
* **The Bluff Reveal:** If the Grandmaster successfully executes its Double-Bluff algorithm against you, it will explicitly tell you in the chat that it knew you were trying to outsmart it.

---

## UI / UX Architecture

The application is built entirely in **Jetpack Compose** using modern Android development standards.
* **State-Driven Rendering:** Utilizing `StateFlow` and `ViewModel`, the UI organically morphs between "Selection Phase" and "Reveal Phase" on a single screen without jarring navigation changes.
* **Tactical Aesthetic:** Custom `TacticalButton` components, sharp corners (`RoundedCornerShape(0.dp)`), and a strict `#1A1D24` dark theme accented with `#F58A27` to mimic AAA-shooter loadout screens.
* **Data Persistence:** Integrated `androidx.datastore.preferences` to track user data and difficulty settings seamlessly.
* **Zero-Latency Splash:** Implementation of the modern `AndroidX Core Splash Screen API` for immediate, flash-free startup.

---

## Installation

Buffer is a fully standalone, offline application requiring **zero system permissions**. 

1. Navigate to the [Releases](../../releases/latest) tab of this repository.
2. Download the latest `buffer-release.apk`.
3. Open the file on your Android device (ensure "Install from Unknown Sources" is enabled).
4. Note on Installation: Because this app is distributed directly via GitHub and not the Google Play Store, Android's Play Protect will likely flag it as an "Unrecognized Developer." This is a standard OS-level security prompt for sideloaded applications. You can safely bypass this by clicking "More Details" -> "Install Anyway."*

---

## Developed By

**Aswin Salish** * **Portfolio:** [aswinsalish.me](https://aswinsalish.me)
* **Stack:** Android SDK, Kotlin, Jetpack Compose

*Distributed under the MIT License.*

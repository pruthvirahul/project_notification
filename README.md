# 📱 Android Notification Filter

A native Android application designed to intercept, log, and filter out unwanted device notifications in real-time. This project demonstrates advanced Android engineering concepts, including system-level background services, asynchronous data handling, and reactive user interfaces.

---

## 💡 The Problem it Solves
In the modern digital age, users are constantly bombarded with notifications ranging from vital messages to intrusive promotional spam. While Android offers some built-in notification channels, they are often coarse-grained and app-specific. 

This app provides **fine-grained, user-defined control** over the notification system. By acting as a middle-layer, it allows users to define custom keywords, Regular Expressions (Regex), and package-specific rules to automatically intercept and dismiss annoying notifications before they even reach the status bar—safeguarding the user's focus and decluttering their digital space.

---

## 🛠️ Key Engineering Principles & Architecture

This application was engineered with modern Android development standards in mind:

### 1. **MVVM (Model-View-ViewModel) Architecture**
The app strictly adheres to the MVVM design pattern, enforcing a clean **Separation of Concerns**. 
- **Model**: Room SQLite database handling data persistence.
- **ViewModel**: Manages UI state and business logic, surviving configuration changes and untangling logic from the UI layer.
- **View**: Jetpack Compose functions that observe StateFlows and react declaratively to changes.

### 2. **Reactive Data Streams (Coroutines & Flow)**
Rather than relying on legacy callbacks or manual UI refreshes, the app utilizes `Kotlin Coroutines` and `StateFlow`:
- The Room database emits a `Flow<List<NotificationLog>>`.
- The `ViewModel` transforms this into a `StateFlow`.
- The Compose UI perfectly synchronizes with the database in real-time without blocking the main thread.

### 3. **System-Level Services & Lifecycle**
The core of the application relies on an extended `NotificationListenerService`. 
- Demonstrates an understanding of Android's strict background execution limits.
- Safely interfaces with System APIs requiring explicit, user-granted security permissions (`BIND_NOTIFICATION_LISTENER_SERVICE`).
- Integrates `PackageManager` to map obscure package domains (e.g., `com.whatsapp`) into human-readable App Names.

### 4. **Modern Declarative UI (Jetpack Compose)**
Built entirely without legacy XML layouts. 
- Features interactive Material Design 3 elements (Cards, Switches, Modals).
- Highly modularized and decoupled components.
- Handles complex conditional states (like disabling specific rules or viewing analytics summaries).

### 5. **Robust Data Persistence (Room ORM)**
Instead of relying on fragile `SharedPreferences` for complex data, the app uses **Room**:
- Built with a scalable schema utilizing cleanly separated Data Access Objects (DAOs).
- Gracefully handles database migrations and synchronous/asynchronous data operations.
- Efficiently stores both Rules (Regex strings, App filters, Booleans) and intercepted Logs.

---

## ✨ Notable Features

- **System-Wide Interception**: Invisibly listens to incoming status bar payloads across the entire OS.
- **Regex Querying Engine**: Supports powerful Regular Expression matching to capture partial strings, prefixes, or complex formatting in notification text.
- **Auto-Dismissal**: Programmatically removes notifications from the system tray if they match a "Dismiss" rule.
- **Analytics Dashboard**: Gives the user a visual summary of how many interruptions the app has saved them from.
- **Ongoing-Notification Filtering**: Intelligently ignores persistent background services (like Music players or VPNs) from cluttering the logs.

---

## 🚀 How to Run Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/pruthvirahul/project_notification.git
   ```
2. **Open in Android Studio**: Open the folder as an existing project.
3. **Sync Gradle**: Allow the IDE to download the required Jetpack components (Compose, Room, Coroutines).
4. **Deploy to a Device/Emulator**: Hit Run. 
   > **Note**: For the app to function, you *must* accept the initial prompt directing you to Android Settings to grant the app "Notification Access".

---

*Authored and engineered as a showcase of modern Native Android Development capabilities in Kotlin.*

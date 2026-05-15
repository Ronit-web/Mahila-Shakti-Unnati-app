# 🌟 Mahila-Shakti Unnati: Microfinance & SHG Management Ecosystem

**Mahila-Shakti Unnati** is a professional-grade, AI-powered Android application designed to empower Women Self-Help Groups (SHGs). It digitizes financial operations, improves transparency, and provides intelligent financial insights to promote rural economic growth.

---

## 📸 Visual Identity & UI
The application features a **modern, premium interface** built with Jetpack Compose.
- **Branded Design**: Optimized with a professional color palette inspired by the "Unnati" logo (Deep Indigo, Vibrant Orange, and Fire gradients).
- **Accessibility**: Focused on high-contrast visuals and readable typography, specifically designed for usability in diverse environments.
- **Modern Grid Layout**: A clean dashboard featuring interactive tiles for seamless navigation.

---

## ✨ Key Features

### 🔐 Advanced Security & Auth
- **Multi-Role Support**: Distinct access levels for Admins, Treasurers, and Members.
- **Firebase Authentication**: Secure login via Email/Password and Google Sign-In.
- **Biometric Login**: Support for Fingerprint and Face ID via Android Biometric Prompt.
- **Encrypted Storage**: Sensitive user data is secured using hardware-backed `EncryptedSharedPreferences`.

### 💰 Financial Management
- **Savings Engine**: Automated tracking of weekly savings, pending deposits, and fine calculation.
- **Loan Module**: Comprehensive loan lifecycle management, including EMI calculators, repayment schedules, and status tracking.
- **Attendance System**: Linked meeting attendance tracking with integrated loan eligibility logic.

### 🤖 Intelligence & Analytics
- **Gemini AI Financial Assistant**: An integrated AI chatbot for financial advice and operational support.
- **AI Risk Assessment**: Intelligent loan request analysis to aid Treasurers in decision-making.
- **Data Visualization**: Custom-built, interactive Canvas charts showing financial growth and attendance trends.

### 📄 Reports & Cloud Sync
- **Smart PDF Generation**: Generate professional SHG monthly summaries and member passbooks.
- **WhatsApp Integration**: Share reports and financial statements directly with members.
- **Offline-First Sync**: Background synchronization using WorkManager and Firebase Firestore, ensuring the app works perfectly without internet.

---

## 🛠 Tech Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository Pattern (Clean Architecture)
- **Local Database**: Room DB (SQLite)
- **Backend/Cloud**: Firebase (Auth, Firestore, Analytics)
- **AI SDK**: Google Generative AI (Gemini Pro)
- **Background Tasks**: Android WorkManager
- **Graphics**: Compose Canvas for custom charts
- **Dependency Management**: Gradle Kotlin DSL (BOM-managed)

---

## 🚀 Setup & Installation Guide

### 1. Prerequisites
- **Android Studio Iguana** or later.
- An **Android device/emulator** (API 24+).
- A **Google Cloud Project** for Gemini AI.
- A **Firebase Project**.

### 2. Firebase Configuration
1. Create a project in [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package: `com.mahilashakti.unnati`.
3. Download `google-services.json` and place it in the `app/` directory.
4. Enable **Authentication** (Email/Password & Google) and **Firestore** (Production Mode).

### 3. Gemini AI Setup
1. Get an API key from [Google AI Studio](https://aistudio.google.com/).
2. Open `local.properties` in the project root.
3. Add the following line: `GEMINI_API_KEY=your_actual_key_here`.
4. The project will automatically use this key via `BuildConfig` (secured by Secrets Gradle Plugin).

### 4. Firestore Security Rules
Deploy the following rules for basic role-based isolation:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 5. Build & Run
1. Clone the repository and open in Android Studio.
2. Click **Sync Project with Gradle Files**.
3. Press **Run (Shift + F10)**.

---

## 📦 Project Structure
- `ai/`: Gemini AI implementation.
- `data/`: Room entities and Data Models.
- `database/`: Room Database and DAOs.
- `firebase/`: Remote data handlers.
- `repository/`: Single source of truth abstraction.
- `ui/`: Compose screens, components, and brand theme.
- `sync/`: WorkManager workers for cloud data sync.
- `viewmodel/`: UI logic and state management.

---

## 📄 License
This project is proprietary. All rights reserved by **R.K.Emulsifier**.

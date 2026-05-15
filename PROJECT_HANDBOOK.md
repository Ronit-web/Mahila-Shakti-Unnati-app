# 📘 Mahila-Shakti Unnati Project Handbook

## 1. Project Overview
**Mahila-Shakti Unnati** is a professional-grade Android microfinance application designed for Women Self Help Groups (SHGs). It provides a complete digital ecosystem for managing members, savings, loans, attendance, and financial analytics with AI-driven insights and secure cloud synchronization.

## 2. Architecture Stack
The application follows the modern **Android Architecture Components** and **Clean Architecture** principles:

- **UI Layer**: Jetpack Compose (Declarative UI) with Material 3 Design.
- **Business Logic**: MVVM (Model-View-ViewModel) pattern.
- **Reactive Streams**: Kotlin Coroutines & StateFlow.
- **Data Layer**: 
  - **Local**: Room Database (SQLite) for offline-first capability.
  - **Cloud**: Firebase Firestore for real-time synchronization.
  - **Security**: EncryptedSharedPreferences (Hardware-backed).
- **Intelligence**: Google Gemini AI (Generative AI) for financial assistance.
- **Background Tasks**: WorkManager for cloud sync and scheduling.

## 3. Directory Structure
```text
com.mahilashakti.unnati
├── ai                  # Gemini AI implementation & Service
├── data                # Data models and Entity definitions
├── database            # Room Database & DAO interfaces
├── firebase            # Firebase Authentication & Firestore logic
├── navigation          # Navigation Graph, Routes & RoleGuard
├── repository          # Data source abstraction layer
├── sync                # WorkManager SyncWorkers
├── ui
│   ├── components      # Reusable UI widgets & CommonUI
│   ├── screens         # Feature-specific Compose screens
│   └── theme           # Material Design 3 tokens & palette
├── utils               # Constants, Helpers & Security utilities
└── viewmodel           # Logic layer for UI state management
```

## 4. Key Modules Summary
1.  **Auth Module**: Multi-role login (Admin/Treasurer/Member) with Firebase.
2.  **Member CRM**: Comprehensive member profiles and management.
3.  **Savings Engine**: Real-time savings tracking and fine calculation.
4.  **Loan Management**: EMI calculators, repayment schedules, and risk assessment.
5.  **Attendance System**: Meeting management with loan eligibility linkage.
6.  **Analytics**: Custom Canvas-based financial growth charts.
7.  **AI Assistant**: Gemini-powered financial chatbot and loan prediction.
8.  **Reports**: Automated PDF generation and WhatsApp sharing.
9.  **Cloud Sync**: Offline-first architecture with background Firestore sync.
10. **Notifications**: Smart reminders using AlarmManager.
11. **Security**: Biometric (Fingerprint/Face) login and encryption.

## 5. Technical Requirements
- **Language**: Kotlin 1.9+
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14.0)
- **Database Version**: 6
- **Architecture**: MVVM + Repository

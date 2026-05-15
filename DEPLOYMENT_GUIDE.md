# 🚀 Deployment & Configuration Guide

Follow these steps to deploy and run the **Mahila-Shakti Unnati** application.

## 1. Firebase Setup
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project named `Mahila-Shakti-Unnati`.
3.  Add an Android App with package name `com.mahilashakti.unnati`.
4.  Download `google-services.json` and place it in the `app/` directory.
5.  **Enable Services**:
    - **Authentication**: Enable Email/Password and Google Sign-In.
    - **Firestore**: Create a database in "Production Mode".
    - **Storage**: (Optional) For profile photos.

## 2. Gemini AI Configuration
1.  Visit the [Google AI Studio](https://aistudio.google.com/).
2.  Generate a new **API Key**.
3.  Open `local.properties` in your project root.
4.  Add the line: `GEMINI_API_KEY=your_actual_key_here`.
    *(Note: This key is injected via BuildConfig and is not committed to Git.)*

## 3. Firestore Security Rules
Use the following rules to ensure role-based data isolation:
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

## 4. Building the Application
1.  Open the project in **Android Studio (Iguana or later)**.
2.  Sync Project with Gradle Files.
3.  **To Run**: Connect a device/emulator and click the "Run" icon.
4.  **To Build APK**:
    - Go to `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
    - The APK will be generated in `app/build/outputs/apk/debug/`.

## 5. First-Time Setup
- Upon first launch, register as an **Admin** to gain full access to all SHG management features.
- Go to **Settings** to enable **Biometric Login** for faster access.

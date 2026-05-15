package com.mahilashakti.unnati.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.mahilashakti.unnati.data.model.Role
import com.mahilashakti.unnati.database.AppDatabase
import com.mahilashakti.unnati.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val database: AppDatabase,
    private val firebaseManager: FirebaseManager
) {
    suspend fun loginWithEmail(email: String, pass: String): Result<AuthResult> {
        return try {
            val result = firebaseManager.auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, pass: String, role: Role): Result<AuthResult> {
        return try {
            val result = firebaseManager.auth.createUserWithEmailAndPassword(email, pass).await()
            // Save role to Firestore
            result.user?.let { user ->
                val userMap = hashMapOf(
                    "id" to user.uid,
                    "email" to email,
                    "role" to role.name
                )
                firebaseManager.firestore.collection("users").document(user.uid).set(userMap).await()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(): Result<AuthResult> {
        return firebaseManager.signInWithGoogle()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return firebaseManager.resetPassword(email)
    }

    fun logout() {
        firebaseManager.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseManager.auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseManager.auth.currentUser
    }
}

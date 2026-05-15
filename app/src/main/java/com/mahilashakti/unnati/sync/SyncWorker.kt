package com.mahilashakti.unnati.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mahilashakti.unnati.database.AppDatabase
import com.mahilashakti.unnati.repository.FirestoreSyncRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure()
        
        val database = AppDatabase.getDatabase(applicationContext)
        val firestore = FirebaseFirestore.getInstance()
        val syncRepository = FirestoreSyncRepository(database, firestore)

        return try {
            syncRepository.syncAll(userId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

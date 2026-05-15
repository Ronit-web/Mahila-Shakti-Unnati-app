package com.mahilashakti.unnati.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mahilashakti.unnati.database.*
import com.mahilashakti.unnati.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) {
    private val memberDao = db.memberDao()
    private val savingsDao = db.savingsDao()
    private val loanDao = db.loanDao()
    private val attendanceDao = db.attendanceDao()

    suspend fun syncAll(userId: String) {
        syncMembers(userId)
        syncSavings(userId)
        syncLoans(userId)
        syncAttendance(userId)
    }

    private suspend fun syncMembers(userId: String) {
        val unsynced = memberDao.getUnsyncedMembers().first()
        unsynced.forEach { member ->
            firestore.collection("users").document(userId)
                .collection("members").document(member.id)
                .set(member).await()
            memberDao.updateMember(member.copy(isSynced = true))
        }
    }

    private suspend fun syncSavings(userId: String) {
        val unsynced = savingsDao.getUnsyncedSavings().first()
        unsynced.forEach { saving ->
            firestore.collection("users").document(userId)
                .collection("savings").document(saving.id)
                .set(saving).await()
            savingsDao.updateSaving(saving.copy(isSynced = true))
        }
    }

    private suspend fun syncLoans(userId: String) {
        val unsynced = loanDao.getUnsyncedLoans().first()
        unsynced.forEach { loan ->
            firestore.collection("users").document(userId)
                .collection("loans").document(loan.id)
                .set(loan).await()
            loanDao.updateLoan(loan.copy(isSynced = true))
        }
    }

    private suspend fun syncAttendance(userId: String) {
        val unsynced = attendanceDao.getUnsyncedAttendance().first()
        unsynced.forEach { attendance ->
            firestore.collection("users").document(userId)
                .collection("attendance").document(attendance.id)
                .set(attendance).await()
            attendanceDao.updateAttendance(attendance.copy(isSynced = true))
        }
    }
}

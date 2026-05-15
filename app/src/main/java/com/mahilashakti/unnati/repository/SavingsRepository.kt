package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.database.SavingsDao
import kotlinx.coroutines.flow.Flow

class SavingsRepository(private val savingsDao: SavingsDao) {

    fun getSavingsForMember(memberId: String): Flow<List<SavingsEntity>> = savingsDao.getSavingsForMember(memberId)

    fun getAllSavings(): Flow<List<SavingsEntity>> = savingsDao.getAllSavings()

    fun getTotalSavingsAmount(): Flow<Double?> = savingsDao.getTotalSavingsAmount()

    fun getTotalFinesAmount(): Flow<Double?> = savingsDao.getTotalFinesAmount()

    suspend fun recordWeeklySavings(savings: List<SavingsEntity>) {
        savingsDao.insertSavings(savings)
    }

    suspend fun recordSaving(saving: SavingsEntity) {
        savingsDao.insertSaving(saving)
    }
}

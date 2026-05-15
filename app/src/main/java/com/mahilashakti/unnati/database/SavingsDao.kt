package com.mahilashakti.unnati.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mahilashakti.unnati.data.model.SavingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {
    @Query("SELECT * FROM savings WHERE memberId = :memberId ORDER BY date DESC")
    fun getSavingsForMember(memberId: String): Flow<List<SavingsEntity>>

    @Query("SELECT * FROM savings ORDER BY date DESC")
    fun getAllSavings(): Flow<List<SavingsEntity>>

    @Query("SELECT SUM(amountPaid) FROM savings")
    fun getTotalSavingsAmount(): Flow<Double?>

    @Query("SELECT SUM(fineAmount) FROM savings")
    fun getTotalFinesAmount(): Flow<Double?>

    @Query("SELECT * FROM savings WHERE isSynced = 0")
    fun getUnsyncedSavings(): Flow<List<SavingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavings(savings: List<SavingsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: SavingsEntity)

    @Update
    suspend fun updateSaving(saving: SavingsEntity)
}

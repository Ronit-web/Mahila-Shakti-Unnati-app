package com.mahilashakti.unnati.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanRepaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    // --- Loans ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanById(loanId: String): LoanEntity?

    @Query("SELECT * FROM loans ORDER BY requestDate DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE isSynced = 0")
    fun getUnsyncedLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE memberId = :memberId ORDER BY requestDate DESC")
    fun getLoansForMember(memberId: String): Flow<List<LoanEntity>>

    @Query("SELECT COUNT(*) FROM loans WHERE memberId = :memberId AND status IN ('PENDING', 'APPROVED')")
    suspend fun getActiveLoansCountForMember(memberId: String): Int

    // --- Repayments ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: LoanRepaymentEntity)

    @Query("SELECT * FROM loan_repayments WHERE loanId = :loanId ORDER BY paymentDate ASC")
    fun getRepaymentsForLoan(loanId: String): Flow<List<LoanRepaymentEntity>>
    
    @Query("SELECT SUM(amountPaid) FROM loan_repayments WHERE loanId = :loanId")
    fun getTotalRepaidForLoan(loanId: String): Flow<Double?>
}

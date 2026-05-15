package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanRepaymentEntity
import com.mahilashakti.unnati.database.LoanDao
import kotlinx.coroutines.flow.Flow

class LoanRepository(private val loanDao: LoanDao) {

    suspend fun insertLoan(loan: LoanEntity) = loanDao.insertLoan(loan)

    suspend fun updateLoan(loan: LoanEntity) = loanDao.updateLoan(loan)

    suspend fun getLoanById(loanId: String): LoanEntity? = loanDao.getLoanById(loanId)

    fun getAllLoans(): Flow<List<LoanEntity>> = loanDao.getAllLoans()

    fun getLoansForMember(memberId: String): Flow<List<LoanEntity>> = loanDao.getLoansForMember(memberId)

    suspend fun getActiveLoansCountForMember(memberId: String): Int = loanDao.getActiveLoansCountForMember(memberId)

    suspend fun insertRepayment(repayment: LoanRepaymentEntity) = loanDao.insertRepayment(repayment)

    fun getRepaymentsForLoan(loanId: String): Flow<List<LoanRepaymentEntity>> = loanDao.getRepaymentsForLoan(loanId)

    fun getTotalRepaidForLoan(loanId: String): Flow<Double?> = loanDao.getTotalRepaidForLoan(loanId)
}

package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.database.LoanDao
import com.mahilashakti.unnati.database.SavingsDao
import com.mahilashakti.unnati.utils.PdfGenerator
import kotlinx.coroutines.flow.first
import java.io.File

class ExportRepository(
    private val pdfGenerator: PdfGenerator,
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao
) {
    suspend fun exportMemberPassbook(member: MemberEntity): File {
        val savings = savingsDao.getSavingsForMember(member.id).first()
        return pdfGenerator.generateMemberPassbook(member, savings)
    }

    suspend fun exportMonthlySummary(): File {
        val totalSavings = savingsDao.getTotalSavingsAmount().first() ?: 0.0
        val allLoans = loanDao.getAllLoans().first()
        val totalLoanAmount = allLoans.sumOf { it.principalAmount }
        val activeLoans = allLoans.count { it.status == "APPROVED" }
        
        return pdfGenerator.generateMonthlySummary(totalSavings, totalLoanAmount, activeLoans)
    }
}

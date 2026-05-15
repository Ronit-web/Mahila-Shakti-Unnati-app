package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.ai.GeminiService
import com.mahilashakti.unnati.database.AttendanceDao
import com.mahilashakti.unnati.database.LoanDao
import com.mahilashakti.unnati.database.SavingsDao
import kotlinx.coroutines.flow.first

class AiRepository(
    private val geminiService: GeminiService,
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao,
    private val attendanceDao: AttendanceDao
) {
    suspend fun getChatResponse(prompt: String): String {
        // Fetch global context
        val totalSavings = savingsDao.getTotalSavingsAmount().first() ?: 0.0
        val allLoans = loanDao.getAllLoans().first()
        val pendingLoans = allLoans.count { it.status == "PENDING" }
        
        val context = "Total SHG Savings: ₹$totalSavings. Total Pending Loans: $pendingLoans. Group size: N/A."
        
        return geminiService.generateResponse(prompt, context)
    }

    suspend fun getMemberRiskAssessment(memberId: String): String {
        val savings = savingsDao.getSavingsForMember(memberId).first()
        val loans = loanDao.getLoansForMember(memberId).first()
        val attendance = attendanceDao.getAttendanceForMember(memberId).first()
        
        val memberContext = "Member ID: $memberId. " +
                "Savings entries: ${savings.size}. " +
                "Loans: ${loans.size} (${loans.map { it.status }}). " +
                "Attendance: ${attendance.count { it.isPresent }}/${attendance.size}."
        
        return geminiService.predictLoanRisk(memberContext)
    }
}

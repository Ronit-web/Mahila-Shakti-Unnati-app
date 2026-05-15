package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.database.AttendanceDao
import com.mahilashakti.unnati.database.LoanDao
import com.mahilashakti.unnati.database.SavingsDao

class AnalyticsRepository(
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao,
    private val attendanceDao: AttendanceDao
) {
    fun getAllSavings() = savingsDao.getAllSavings()
    fun getAllLoans() = loanDao.getAllLoans()
    fun getAllAttendance() = attendanceDao.getAllAttendance()
}

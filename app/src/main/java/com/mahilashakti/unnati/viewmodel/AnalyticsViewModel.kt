package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.repository.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChartDataPoint(val label: String, val value: Float)

data class AnalyticsState(
    val savingsOverTime: List<ChartDataPoint> = emptyList(),
    val attendanceTrends: List<ChartDataPoint> = emptyList(),
    val loanStats: Map<String, Int> = emptyMap()
)

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {

    val analyticsState: StateFlow<AnalyticsState> = combine(
        repository.getAllSavings(),
        repository.getAllLoans(),
        repository.getAllAttendance()
    ) { savings, loans, attendance ->
        
        // 1. Savings growth over time (group by month)
        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        val savingsPoints = savings.groupBy { sdf.format(Date(it.date)) }
            .map { (month, list) -> ChartDataPoint(month, list.sumOf { it.amountPaid }.toFloat()) }
            .takeLast(6)

        // 2. Attendance trends
        val attendancePoints = attendance.groupBy { sdf.format(Date(it.date)) }
            .map { (month, list) -> 
                val presentCount = list.count { it.isPresent }
                val rate = if (list.isNotEmpty()) (presentCount.toFloat() / list.size.toFloat()) * 100 else 0f
                ChartDataPoint(month, rate)
            }.takeLast(6)

        // 3. Loan stats
        val loanMap = loans.groupBy { it.status }.mapValues { it.value.size }

        AnalyticsState(
            savingsOverTime = savingsPoints,
            attendanceTrends = attendancePoints,
            loanStats = loanMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsState())
}

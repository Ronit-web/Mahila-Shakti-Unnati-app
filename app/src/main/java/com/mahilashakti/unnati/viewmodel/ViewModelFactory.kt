package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahilashakti.unnati.repository.AiRepository
import com.mahilashakti.unnati.repository.AnalyticsRepository
import com.mahilashakti.unnati.repository.AttendanceRepository
import com.mahilashakti.unnati.repository.ExportRepository
import com.mahilashakti.unnati.repository.LoanRepository
import com.mahilashakti.unnati.repository.MemberRepository
import com.mahilashakti.unnati.repository.SavingsRepository
import com.mahilashakti.unnati.repository.UserRepository
import com.mahilashakti.unnati.utils.VoiceToTextParser
import com.mahilashakti.unnati.utils.ReminderScheduler
import com.mahilashakti.unnati.utils.SecurityUtils
import android.content.Context

class ViewModelFactory(
    private val context: Context,
    private val userRepository: UserRepository,
    private val memberRepository: MemberRepository,
    private val savingsRepository: SavingsRepository,
    private val loanRepository: LoanRepository,
    private val attendanceRepository: AttendanceRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val aiRepository: AiRepository,
    private val voiceParser: VoiceToTextParser,
    private val exportRepository: ExportRepository,
    private val reminderScheduler: ReminderScheduler,
    private val securityUtils: SecurityUtils
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(memberRepository) as T
        }
        if (modelClass.isAssignableFrom(SavingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavingsViewModel(savingsRepository, memberRepository) as T
        }
        if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanViewModel(loanRepository, attendanceRepository) as T
        }
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(attendanceRepository, memberRepository) as T
        }
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(analyticsRepository) as T
        }
        if (modelClass.isAssignableFrom(AiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiViewModel(aiRepository, voiceParser) as T
        }
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(exportRepository) as T
        }
        if (modelClass.isAssignableFrom(SyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SyncViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(reminderScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

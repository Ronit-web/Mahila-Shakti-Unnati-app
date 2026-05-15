package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import com.mahilashakti.unnati.utils.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class ReminderSettingsState(
    val isMeetingReminderEnabled: Boolean = true,
    val isSavingsReminderEnabled: Boolean = true,
    val isLoanReminderEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0
)

class NotificationViewModel(private val scheduler: ReminderScheduler) : ViewModel() {

    private val _settings = MutableStateFlow(ReminderSettingsState())
    val settings = _settings.asStateFlow()

    fun updateMeetingReminder(enabled: Boolean) {
        _settings.value = _settings.value.copy(isMeetingReminderEnabled = enabled)
        if (enabled) scheduleGenericMeetingReminder() else scheduler.cancelReminder(101)
    }

    fun updateSavingsReminder(enabled: Boolean) {
        _settings.value = _settings.value.copy(isSavingsReminderEnabled = enabled)
        // Scheduling logic would go here
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _settings.value = _settings.value.copy(reminderHour = hour, reminderMinute = minute)
    }

    private fun scheduleGenericMeetingReminder() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, _settings.value.reminderHour)
            set(Calendar.MINUTE, _settings.value.reminderMinute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        
        scheduler.scheduleReminder(
            id = 101,
            title = "SHG Meeting Reminder",
            message = "Your weekly meeting starts soon. Please be on time!",
            timeInMillis = calendar.timeInMillis
        )
    }
}

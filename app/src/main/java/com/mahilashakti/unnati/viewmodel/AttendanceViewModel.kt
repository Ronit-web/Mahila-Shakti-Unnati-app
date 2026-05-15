package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.data.model.AttendanceEntity
import com.mahilashakti.unnati.repository.AttendanceRepository
import com.mahilashakti.unnati.repository.MemberRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    val allAttendance: StateFlow<List<AttendanceEntity>> = attendanceRepository.getAllAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMembers = memberRepository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordBulkAttendance(entries: List<AttendanceEntity>) {
        viewModelScope.launch {
            attendanceRepository.insertAttendance(entries)
        }
    }

    fun getMemberAttendanceHistory(memberId: String): StateFlow<List<AttendanceEntity>> {
        return attendanceRepository.getAttendanceForMember(memberId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

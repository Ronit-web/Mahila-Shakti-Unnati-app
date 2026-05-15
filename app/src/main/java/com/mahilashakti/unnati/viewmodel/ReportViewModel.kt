package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.repository.ExportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class ReportState {
    object Idle : ReportState()
    object Generating : ReportState()
    data class Success(val file: File) : ReportState()
    data class Error(val message: String) : ReportState()
}

class ReportViewModel(private val exportRepository: ExportRepository) : ViewModel() {

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState = _reportState.asStateFlow()

    fun resetState() {
        _reportState.value = ReportState.Idle
    }

    fun generateMemberPassbook(member: MemberEntity) {
        viewModelScope.launch {
            _reportState.value = ReportState.Generating
            try {
                val file = exportRepository.exportMemberPassbook(member)
                _reportState.value = ReportState.Success(file)
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(e.message ?: "Failed to generate passbook")
            }
        }
    }

    fun generateMonthlySummary() {
        viewModelScope.launch {
            _reportState.value = ReportState.Generating
            try {
                val file = exportRepository.exportMonthlySummary()
                _reportState.value = ReportState.Success(file)
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(e.message ?: "Failed to generate monthly summary")
            }
        }
    }
}

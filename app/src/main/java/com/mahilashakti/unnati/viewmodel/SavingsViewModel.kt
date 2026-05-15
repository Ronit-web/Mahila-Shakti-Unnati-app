package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.repository.MemberRepository
import com.mahilashakti.unnati.repository.SavingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavingsViewModel(
    private val savingsRepository: SavingsRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    // Global Totals
    val totalSavingsAmount: StateFlow<Double?> = savingsRepository.getTotalSavingsAmount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalFinesAmount: StateFlow<Double?> = savingsRepository.getTotalFinesAmount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allSavingsHistory: StateFlow<List<SavingsEntity>> = savingsRepository.getAllSavings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // For recording screen - we need all members to present a checklist
    val allMembers = memberRepository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // State for creating new savings entries
    val defaultSavingsAmount = 100.0
    val defaultFineAmount = 50.0

    fun recordBulkSavings(entries: List<SavingsEntity>) {
        viewModelScope.launch {
            savingsRepository.recordWeeklySavings(entries)
        }
    }

    fun getMemberSavingsHistory(memberId: String): StateFlow<List<SavingsEntity>> {
        return savingsRepository.getSavingsForMember(memberId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

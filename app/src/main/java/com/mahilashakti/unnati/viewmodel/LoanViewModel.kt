package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanRepaymentEntity
import com.mahilashakti.unnati.data.model.LoanStatus
import com.mahilashakti.unnati.repository.LoanRepository
import com.mahilashakti.unnati.repository.AttendanceRepository
import com.mahilashakti.unnati.utils.EmiCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class LoanUIState {
    object Idle : LoanUIState()
    object Loading : LoanUIState()
    object Success : LoanUIState()
    data class Error(val message: String) : LoanUIState()
}

class LoanViewModel(
    private val loanRepository: LoanRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoanUIState>(LoanUIState.Idle)
    val uiState: StateFlow<LoanUIState> = _uiState.asStateFlow()

    val allLoans: StateFlow<List<LoanEntity>> = loanRepository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun resetState() {
        _uiState.value = LoanUIState.Idle
    }

    fun requestLoan(memberId: String, principal: Double, interestRate: Double, durationMonths: Int) {
        if (principal <= 0 || durationMonths <= 0) {
            _uiState.value = LoanUIState.Error("Invalid loan parameters")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoanUIState.Loading
            try {
                // Check Active Loans
                val activeCount = loanRepository.getActiveLoansCountForMember(memberId)
                if (activeCount > 0) {
                    _uiState.value = LoanUIState.Error("Member already has an active or pending loan.")
                    return@launch
                }

                // Check Attendance Eligibility (Must be >= 75%)
                val totalMeetings = attendanceRepository.getTotalMeetingsForMember(memberId)
                if (totalMeetings >= 4) { // Only enforce after 4 meetings
                    val attended = attendanceRepository.getAttendedMeetingsForMember(memberId)
                    val attendancePercentage = (attended.toDouble() / totalMeetings.toDouble()) * 100
                    if (attendancePercentage < 75.0) {
                        _uiState.value = LoanUIState.Error("Loan denied: Attendance is below 75% (${String.format("%.1f", attendancePercentage)}%)")
                        return@launch
                    }
                }

                val emi = EmiCalculator.calculateEMI(principal, interestRate, durationMonths)
                val loan = LoanEntity(
                    memberId = memberId,
                    principalAmount = principal,
                    interestRate = interestRate,
                    durationMonths = durationMonths,
                    emiAmount = emi,
                    status = LoanStatus.PENDING.name
                )
                loanRepository.insertLoan(loan)
                _uiState.value = LoanUIState.Success
            } catch (e: Exception) {
                _uiState.value = LoanUIState.Error(e.message ?: "Failed to request loan")
            }
        }
    }

    fun approveLoan(loan: LoanEntity) {
        viewModelScope.launch {
            try {
                val approvedLoan = loan.copy(
                    status = LoanStatus.APPROVED.name,
                    approvalDate = System.currentTimeMillis()
                )
                loanRepository.updateLoan(approvedLoan)
            } catch (e: Exception) {
                _uiState.value = LoanUIState.Error(e.message ?: "Failed to approve loan")
            }
        }
    }

    fun rejectLoan(loan: LoanEntity) {
        viewModelScope.launch {
            try {
                val rejectedLoan = loan.copy(status = LoanStatus.REJECTED.name)
                loanRepository.updateLoan(rejectedLoan)
            } catch (e: Exception) {
                _uiState.value = LoanUIState.Error(e.message ?: "Failed to reject loan")
            }
        }
    }

    fun recordRepayment(loan: LoanEntity, amountPaid: Double, currentEmiNumber: Int) {
        viewModelScope.launch {
            _uiState.value = LoanUIState.Loading
            try {
                val repayment = LoanRepaymentEntity(
                    loanId = loan.id,
                    memberId = loan.memberId,
                    amountPaid = amountPaid,
                    emiNumber = currentEmiNumber
                )
                loanRepository.insertRepayment(repayment)
                
                // Check if paid off
                // We simplify by just tracking EMIs paid. In a real app, we'd check total repayment sum vs (EMI * duration).
                if (currentEmiNumber >= loan.durationMonths) {
                    loanRepository.updateLoan(loan.copy(status = LoanStatus.PAID_OFF.name))
                }
                
                _uiState.value = LoanUIState.Success
            } catch (e: Exception) {
                _uiState.value = LoanUIState.Error(e.message ?: "Failed to record repayment")
            }
        }
    }

    suspend fun getLoanById(loanId: String): LoanEntity? {
        return loanRepository.getLoanById(loanId)
    }

    fun getRepaymentsForLoan(loanId: String): StateFlow<List<LoanRepaymentEntity>> {
        return loanRepository.getRepaymentsForLoan(loanId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

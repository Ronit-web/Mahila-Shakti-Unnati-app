package com.mahilashakti.unnati.ui.screens.loan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanStatus
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.LoanUIState
import com.mahilashakti.unnati.viewmodel.LoanViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mahilashakti.unnati.viewmodel.AiViewModel
import com.mahilashakti.unnati.ui.screens.ai.LoanRiskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailsScreen(
    loanId: String,
    loanViewModel: LoanViewModel,
    aiViewModel: AiViewModel,
    onNavigateBack: () -> Unit
) {
    val loans by loanViewModel.allLoans.collectAsState()
    val loan = loans.find { it.id == loanId }
    
    val riskResult by aiViewModel.riskResult.collectAsState()
    val aiLoading by aiViewModel.isLoading.collectAsState()
    val repayments by loanViewModel.getRepaymentsForLoan(loanId).collectAsState()
    val uiState by loanViewModel.uiState.collectAsState()

    LaunchedEffect(loanId, uiState) {
        // Refresh handled by collectAsState
    }

    if (loan == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentLoan = loan!!
    val totalRepaid = repayments.sumOf { it.amountPaid }
    val totalExpected = currentLoan.emiAmount * currentLoan.durationMonths

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status:", style = MaterialTheme.typography.bodyLarge)
                        Text(currentLoan.status, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Principal: ₹${currentLoan.principalAmount}")
                    Text("Interest Rate: ${currentLoan.interestRate}%")
                    Text("Duration: ${currentLoan.durationMonths} months")
                    Text("EMI: ₹${String.format("%.2f", currentLoan.emiAmount)}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentLoan.status == LoanStatus.PENDING.name) {
                LoanRiskCard(
                    riskResult = riskResult,
                    onAssess = { aiViewModel.assessRisk(currentLoan.memberId) },
                    isLoading = aiLoading
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { loanViewModel.rejectLoan(currentLoan) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reject")
                    }
                    Button(
                        onClick = { loanViewModel.approveLoan(currentLoan) },
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("Approve")
                    }
                }
            } else if (currentLoan.status == LoanStatus.APPROVED.name) {
                Text("Repayment Progress", style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(
                    progress = if (totalExpected > 0) (totalRepaid / totalExpected).toFloat() else 0f,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                Text("Repaid: ₹${String.format("%.2f", totalRepaid)} / ₹${String.format("%.2f", totalExpected)}")

                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    text = "Record EMI Payment (₹${String.format("%.2f", currentLoan.emiAmount)})",
                    onClick = {
                        loanViewModel.recordRepayment(currentLoan, currentLoan.emiAmount, repayments.size + 1)
                    },
                    isLoading = uiState is LoanUIState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("History", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    items(repayments) { rep ->
                        ListItem(
                            headlineContent = { Text("EMI #${rep.emiNumber}") },
                            supportingContent = { Text(sdf.format(Date(rep.paymentDate))) },
                            trailingContent = { Text("₹${rep.amountPaid}") }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

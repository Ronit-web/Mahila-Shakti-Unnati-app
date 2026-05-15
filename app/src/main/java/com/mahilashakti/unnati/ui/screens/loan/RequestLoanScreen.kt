package com.mahilashakti.unnati.ui.screens.loan

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.CustomTextField
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.utils.EmiCalculator
import com.mahilashakti.unnati.viewmodel.LoanUIState
import com.mahilashakti.unnati.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestLoanScreen(
    memberId: String,
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("12.0") } // 12% default
    var durationMonths by remember { mutableStateOf("12") } // 12 months default

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoanUIState.Success -> {
                viewModel.resetState()
                Toast.makeText(context, "Loan requested successfully!", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
            is LoanUIState.Error -> {
                Toast.makeText(context, (uiState as LoanUIState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val calculatedEmi = remember(principal, interestRate, durationMonths) {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = interestRate.toDoubleOrNull() ?: 0.0
        val n = durationMonths.toIntOrNull() ?: 0
        EmiCalculator.calculateEMI(p, r, n)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request New Loan") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                value = principal,
                onValueChange = { principal = it },
                label = "Principal Amount (₹)",
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomTextField(
                value = interestRate,
                onValueChange = { interestRate = it },
                label = "Annual Interest Rate (%)",
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = durationMonths,
                onValueChange = { durationMonths = it },
                label = "Duration (Months)",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("EMI Breakdown", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "₹ ${String.format("%.2f", calculatedEmi)} / month",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total to repay: ₹${String.format("%.2f", calculatedEmi * (durationMonths.toIntOrNull() ?: 0))}")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Submit Request",
                onClick = {
                    viewModel.requestLoan(
                        memberId = memberId,
                        principal = principal.toDoubleOrNull() ?: 0.0,
                        interestRate = interestRate.toDoubleOrNull() ?: 0.0,
                        durationMonths = durationMonths.toIntOrNull() ?: 0
                    )
                },
                isLoading = uiState is LoanUIState.Loading
            )
        }
    }
}

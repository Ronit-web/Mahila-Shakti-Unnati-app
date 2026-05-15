package com.mahilashakti.unnati.ui.screens.loan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanStatus
import com.mahilashakti.unnati.viewmodel.LoanViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDashboardScreen(
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val allLoans by viewModel.allLoans.collectAsState()

    val pendingLoans = allLoans.filter { it.status == LoanStatus.PENDING.name }
    val activeLoans = allLoans.filter { it.status == LoanStatus.APPROVED.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Management") },
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
            Text("Pending Requests (${pendingLoans.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (pendingLoans.isEmpty()) {
                Text("No pending requests.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(pendingLoans) { loan ->
                        LoanCard(loan, onNavigateToDetails)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            Text("Active Loans (${activeLoans.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (activeLoans.isEmpty()) {
                Text("No active loans.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn {
                    items(activeLoans) { loan ->
                        LoanCard(loan, onNavigateToDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun LoanCard(loan: LoanEntity, onClick: (String) -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateToDisplay = if (loan.status == LoanStatus.PENDING.name) loan.requestDate else (loan.approvalDate ?: loan.requestDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(loan.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Amount: ₹${loan.principalAmount}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("EMI: ₹${String.format("%.2f", loan.emiAmount)} / mo", style = MaterialTheme.typography.bodyMedium)
                Text("Date: ${sdf.format(Date(dateToDisplay))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Badge(
                containerColor = when(loan.status) {
                    LoanStatus.PENDING.name -> MaterialTheme.colorScheme.tertiary
                    LoanStatus.APPROVED.name -> MaterialTheme.colorScheme.primary
                    LoanStatus.PAID_OFF.name -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
            ) {
                Text(loan.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

package com.mahilashakti.unnati.ui.screens.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.viewmodel.SavingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSavingsHistoryScreen(
    memberId: String,
    viewModel: SavingsViewModel,
    onNavigateBack: () -> Unit
) {
    val history by viewModel.getMemberSavingsHistory(memberId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Savings History") },
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
            val totalPaid = history.filter { it.isPaid }.sumOf { it.amountPaid }
            val totalFines = history.filter { !it.isPaid }.sumOf { it.fineAmount }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Saved", style = MaterialTheme.typography.bodyMedium)
                        Text("₹ $totalPaid", style = MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Fines", style = MaterialTheme.typography.bodyMedium)
                        Text("₹ $totalFines", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No history found.")
                }
            } else {
                LazyColumn {
                    items(history) { saving ->
                        TransactionCard(saving)
                    }
                }
            }
        }
    }
}

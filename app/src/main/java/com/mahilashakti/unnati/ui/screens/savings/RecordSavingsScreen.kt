package com.mahilashakti.unnati.ui.screens.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.viewmodel.SavingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSavingsScreen(
    viewModel: SavingsViewModel,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.allMembers.collectAsState()
    
    // Track states locally before saving
    val paidStatusMap = remember { mutableStateMapOf<String, Boolean>() }
    
    LaunchedEffect(members) {
        if (paidStatusMap.isEmpty()) {
            members.forEach { paidStatusMap[it.id] = true } // default all to paid
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Savings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val expectedTotal = members.sumOf { 
                        if (paidStatusMap[it.id] == true) viewModel.defaultSavingsAmount else viewModel.defaultFineAmount
                    }
                    Text("Total: ₹$expectedTotal", style = MaterialTheme.typography.titleMedium)
                    
                    Button(onClick = {
                        val entries = members.map { member ->
                            val isPaid = paidStatusMap[member.id] ?: true
                            SavingsEntity(
                                memberId = member.id,
                                amountPaid = if (isPaid) viewModel.defaultSavingsAmount else 0.0,
                                isPaid = isPaid,
                                fineAmount = if (isPaid) 0.0 else viewModel.defaultFineAmount
                            )
                        }
                        viewModel.recordBulkSavings(entries)
                        onNavigateBack()
                    }) {
                        Text("Save All")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(members) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(member.name, style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = paidStatusMap[member.id] ?: true,
                            onCheckedChange = { isPaid -> paidStatusMap[member.id] = isPaid }
                        )
                    }
                }
            }
        }
    }
}

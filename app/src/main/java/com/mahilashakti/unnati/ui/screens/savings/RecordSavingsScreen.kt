package com.mahilashakti.unnati.ui.screens.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.ui.components.ModernCard
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.SavingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSavingsScreen(
    viewModel: SavingsViewModel,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.allMembers.collectAsState()
    val paidStatusMap = remember { mutableStateMapOf<String, Boolean>() }
    
    LaunchedEffect(members) {
        if (paidStatusMap.isEmpty()) {
            members.forEach { paidStatusMap[it.id] = true }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Weekly Savings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 12.dp,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val expectedTotal = members.sumOf { 
                        if (paidStatusMap[it.id] == true) viewModel.defaultSavingsAmount else viewModel.defaultFineAmount
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Collection", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "₹ $expectedTotal", 
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Save All Records",
                        icon = Icons.Filled.Save,
                        onClick = {
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
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Member List", 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
            items(members) { member ->
                val isPaid = paidStatusMap[member.id] ?: true
                ModernCard(
                    containerColor = if (isPaid) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                member.name, 
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                if (isPaid) "Regular Savings" else "Pending (Fine Applied)",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                            )
                        }
                        Switch(
                            checked = isPaid,
                            onCheckedChange = { paidStatusMap[member.id] = it },
                            thumbContent = if (isPaid) {
                                { Icon(Icons.Filled.CheckCircle, null, Modifier.size(SwitchDefaults.IconSize)) }
                            } else null
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

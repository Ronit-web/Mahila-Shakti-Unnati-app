package com.mahilashakti.unnati.ui.screens.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.ui.components.BrandedHeaderCard
import com.mahilashakti.unnati.ui.components.ModernCard
import com.mahilashakti.unnati.viewmodel.SavingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsDashboardScreen(
    viewModel: SavingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecordSavings: () -> Unit
) {
    val totalSavings by viewModel.totalSavingsAmount.collectAsState()
    val totalFines by viewModel.totalFinesAmount.collectAsState()
    val history by viewModel.allSavingsHistory.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Savings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRecordSavings,
                icon = { Icon(Icons.Filled.Add, "Record") },
                text = { Text("New Record") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            BrandedHeaderCard(
                title = "Total SHG Funds",
                subtitle = "Combined Savings & Fines",
                value = "₹ ${String.format("%.2f", (totalSavings ?: 0.0) + (totalFines ?: 0.0))}",
                icon = Icons.Filled.AccountBalance
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Savings",
                    amount = "₹${totalSavings ?: 0.0}",
                    color = MaterialTheme.colorScheme.primary
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Fines",
                    amount = "₹${totalFines ?: 0.0}",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Recent Transactions", 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (history.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { saving ->
                        TransactionItem(saving)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(modifier: Modifier, title: String, amount: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = color)
            Text(text = amount, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
fun TransactionItem(saving: SavingsEntity) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = if (saving.isPaid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = if (saving.isPaid) Icons.Filled.ArrowUpward else Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = if (saving.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val statusText = if (saving.isPaid) "Savings Paid" else "Pending with Fine"
                    Text(text = statusText, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(text = sdf.format(Date(saving.date)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = "₹ ${saving.amountPaid + saving.fineAmount}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (saving.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.History, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No transactions yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

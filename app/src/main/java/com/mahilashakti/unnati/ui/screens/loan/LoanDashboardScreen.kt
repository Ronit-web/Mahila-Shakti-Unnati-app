package com.mahilashakti.unnati.ui.screens.loan

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
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanStatus
import com.mahilashakti.unnati.ui.components.BrandedHeaderCard
import com.mahilashakti.unnati.ui.components.ModernCard
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
            CenterAlignedTopAppBar(
                title = { Text("Loans", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
                title = "Loan Management",
                subtitle = "Active & Pending Requests",
                value = "${activeLoans.size} Active",
                icon = Icons.Filled.MonetizationOn
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (pendingLoans.isNotEmpty()) {
                    item {
                        Text(
                            "Pending Requests", 
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(pendingLoans) { loan ->
                        LoanItem(loan, onNavigateToDetails)
                    }
                }

                item {
                    Text(
                        "Active Loans", 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }

                if (activeLoans.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No active loans.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(activeLoans) { loan ->
                        LoanItem(loan, onNavigateToDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun LoanItem(loan: LoanEntity, onClick: (String) -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateToDisplay = if (loan.status == LoanStatus.PENDING.name) loan.requestDate else (loan.approvalDate ?: loan.requestDate)

    ModernCard(onClick = { onClick(loan.id) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = when(loan.status) {
                    LoanStatus.PENDING.name -> MaterialTheme.colorScheme.tertiaryContainer
                    LoanStatus.APPROVED.name -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = when(loan.status) {
                        LoanStatus.PENDING.name -> MaterialTheme.colorScheme.tertiary
                        LoanStatus.APPROVED.name -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "₹ ${loan.principalAmount}", 
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "EMI: ₹${String.format("%.2f", loan.emiAmount)}/mo", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusChip(status = loan.status)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sdf.format(Date(dateToDisplay)), 
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when(status) {
        LoanStatus.PENDING.name -> MaterialTheme.colorScheme.tertiary
        LoanStatus.APPROVED.name -> MaterialTheme.colorScheme.primary
        LoanStatus.PAID_OFF.name -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = color)
        )
    }
}

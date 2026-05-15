package com.mahilashakti.unnati.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.utils.ShareUtils
import com.mahilashakti.unnati.viewmodel.ReportState
import com.mahilashakti.unnati.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDashboardScreen(
    viewModel: ReportViewModel,
    onNavigateBack: () -> Unit
) {
    val reportState by viewModel.reportState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(reportState) {
        if (reportState is ReportState.Success) {
            ShareUtils.sharePdf(context, (reportState as ReportState.Success).file, "SHG Report")
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Exports") },
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
            Text("General Reports", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            ReportActionCard(
                title = "Monthly SHG Summary",
                description = "Generate a PDF summary of all savings and loans for the current month.",
                icon = Icons.Filled.Description,
                onClick = { viewModel.generateMonthlySummary() },
                isLoading = reportState is ReportState.Generating
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            if (reportState is ReportState.Error) {
                Text((reportState as ReportState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ReportActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isLoading
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Filled.Share, contentDescription = "Share")
            }
        }
    }
}

package com.mahilashakti.unnati.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.BrandedHeaderCard
import com.mahilashakti.unnati.ui.components.ModernCard
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
            CenterAlignedTopAppBar(
                title = { Text("Reports", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                title = "Exports",
                subtitle = "Generate Financial Statements",
                icon = Icons.Filled.Assessment
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Available Reports", 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )

            ReportActionItem(
                title = "Monthly SHG Summary",
                description = "Full overview of savings and loans for this month.",
                icon = Icons.Filled.Description,
                onClick = { viewModel.generateMonthlySummary() },
                isLoading = reportState is ReportState.Generating
            )

            if (reportState is ReportState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (reportState as ReportState.Error).message, 
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ReportActionItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    ModernCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = description, 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

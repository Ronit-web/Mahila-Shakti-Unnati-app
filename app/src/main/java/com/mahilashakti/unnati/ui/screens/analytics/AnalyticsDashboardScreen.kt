package com.mahilashakti.unnati.ui.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.ModernBarChart
import com.mahilashakti.unnati.ui.components.ModernLineChart
import com.mahilashakti.unnati.viewmodel.AnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.analyticsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights & Analytics") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Savings Growth Chart
            AnalyticsCard(title = "Savings Growth (Last 6 Months)") {
                ModernLineChart(
                    data = state.savingsOverTime,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attendance Rate Chart
            AnalyticsCard(title = "Attendance Rate (%)") {
                ModernLineChart(
                    data = state.attendanceTrends,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    lineColor = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loan Status Comparison
            AnalyticsCard(title = "Loan Status Overview") {
                val loanData = state.loanStats.map { (status, count) -> 
                    com.mahilashakti.unnati.viewmodel.ChartDataPoint(status, count.toFloat())
                }
                ModernBarChart(
                    data = loanData,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

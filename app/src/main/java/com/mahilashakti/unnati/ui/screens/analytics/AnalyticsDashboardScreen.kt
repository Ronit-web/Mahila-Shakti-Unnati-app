package com.mahilashakti.unnati.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.BrandedHeaderCard
import com.mahilashakti.unnati.ui.components.ModernBarChart
import com.mahilashakti.unnati.ui.components.ModernCard
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
            CenterAlignedTopAppBar(
                title = { Text("Insights", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            BrandedHeaderCard(
                title = "Group Performance",
                subtitle = "Growth & Engagement Trends",
                icon = Icons.AutoMirrored.Filled.TrendingUp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Savings Growth Chart
            AnalyticsSection(title = "Savings Growth (Last 6 Months)") {
                ModernLineChart(
                    data = state.savingsOverTime,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Attendance Rate Chart
            AnalyticsSection(title = "Attendance Trends (%)") {
                ModernLineChart(
                    data = state.attendanceTrends,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    lineColor = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loan Status Comparison
            AnalyticsSection(title = "Loan Disbursement Overview") {
                val loanData = state.loanStats.map { (status, count) -> 
                    com.mahilashakti.unnati.viewmodel.ChartDataPoint(status, count.toFloat())
                }
                ModernBarChart(
                    data = loanData,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnalyticsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title, 
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
        ModernCard {
            content()
        }
    }
}

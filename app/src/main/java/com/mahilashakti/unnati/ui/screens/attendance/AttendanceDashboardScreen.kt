package com.mahilashakti.unnati.ui.screens.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDashboardScreen(
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecordAttendance: () -> Unit
) {
    val allAttendance by viewModel.allAttendance.collectAsState()

    // Group by distinct dates (rounded to day)
    val groupedByDate = allAttendance.groupBy { 
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.date))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRecordAttendance,
                icon = { Icon(Icons.Filled.Add, "Record Attendance") },
                text = { Text("Record Weekly Meeting") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Meetings Recorded", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${groupedByDate.size}", style = MaterialTheme.typography.displaySmall)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Meeting History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (groupedByDate.isEmpty()) {
                Text("No meetings recorded yet.")
            } else {
                LazyColumn {
                    items(groupedByDate.entries.toList().sortedByDescending { it.key }) { entry ->
                        val dateString = entry.key
                        val records = entry.value
                        val presentCount = records.count { it.isPresent }
                        val totalMembers = records.size
                        
                        ListItem(
                            headlineContent = { Text("Date: $dateString") },
                            supportingContent = { Text("Present: $presentCount / $totalMembers") }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

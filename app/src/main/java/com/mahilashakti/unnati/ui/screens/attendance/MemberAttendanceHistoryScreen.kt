package com.mahilashakti.unnati.ui.screens.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
fun MemberAttendanceHistoryScreen(
    memberId: String,
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val history by viewModel.getMemberAttendanceHistory(memberId).collectAsState()

    val totalMeetings = history.size
    val attended = history.count { it.isPresent }
    val percentage = if (totalMeetings > 0) (attended.toDouble() / totalMeetings.toDouble()) * 100 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance History") },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overall Attendance", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${String.format("%.1f", percentage)}%", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Attended $attended out of $totalMeetings meetings")
                    if (totalMeetings >= 4 && percentage < 75.0) {
                        Text("WARNING: Below 75%. Loan eligibility revoked.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Meeting Log", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (history.isEmpty()) {
                Text("No attendance recorded yet.")
            } else {
                LazyColumn {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    items(history) { record ->
                        ListItem(
                            headlineContent = { Text(sdf.format(Date(record.date))) },
                            trailingContent = { 
                                Text(
                                    if (record.isPresent) "Present" else "Absent",
                                    color = if (record.isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                ) 
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

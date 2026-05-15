package com.mahilashakti.unnati.ui.screens.attendance

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
import com.mahilashakti.unnati.data.model.AttendanceEntity
import com.mahilashakti.unnati.viewmodel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.allMembers.collectAsState()
    val presentStatusMap = remember { mutableStateMapOf<String, Boolean>() }
    
    LaunchedEffect(members) {
        if (presentStatusMap.isEmpty()) {
            members.forEach { presentStatusMap[it.id] = true } // default all to present
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Attendance") },
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
                    val presentCount = members.count { presentStatusMap[it.id] == true }
                    Text("Present: $presentCount / ${members.size}", style = MaterialTheme.typography.titleMedium)
                    
                    Button(onClick = {
                        val currentTime = System.currentTimeMillis()
                        val entries = members.map { member ->
                            AttendanceEntity(
                                memberId = member.id,
                                date = currentTime,
                                isPresent = presentStatusMap[member.id] ?: true
                            )
                        }
                        viewModel.recordBulkAttendance(entries)
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
                        Column {
                            Text(member.name, style = MaterialTheme.typography.bodyLarge)
                            Text(if (presentStatusMap[member.id] == true) "Present" else "Absent", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (presentStatusMap[member.id] == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                        Switch(
                            checked = presentStatusMap[member.id] ?: true,
                            onCheckedChange = { isPresent -> presentStatusMap[member.id] = isPresent }
                        )
                    }
                }
            }
        }
    }
}

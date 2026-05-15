package com.mahilashakti.unnati.ui.screens.attendance

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
import com.mahilashakti.unnati.data.model.AttendanceEntity
import com.mahilashakti.unnati.ui.components.ModernCard
import com.mahilashakti.unnati.ui.components.PrimaryButton
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
            members.forEach { presentStatusMap[it.id] = true }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meeting Attendance", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                    val presentCount = members.count { presentStatusMap[it.id] == true }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Attendance Summary", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "$presentCount / ${members.size} Present", 
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Save Attendance",
                        icon = Icons.Filled.Save,
                        onClick = {
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
                    "Roll Call", 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
            items(members) { member ->
                val isPresent = presentStatusMap[member.id] ?: true
                ModernCard(
                    containerColor = if (isPresent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
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
                                if (isPresent) "Present" else "Absent",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        Switch(
                            checked = isPresent,
                            onCheckedChange = { presentStatusMap[member.id] = it },
                            thumbContent = if (isPresent) {
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

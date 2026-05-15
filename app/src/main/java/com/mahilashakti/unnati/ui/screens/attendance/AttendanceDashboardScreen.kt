package com.mahilashakti.unnati.ui.screens.attendance

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.BrandedHeaderCard
import com.mahilashakti.unnati.ui.components.ModernCard
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
            CenterAlignedTopAppBar(
                title = { Text("Attendance", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                onClick = onNavigateToRecordAttendance,
                icon = { Icon(Icons.Filled.GroupAdd, "Record") },
                text = { Text("Record Meeting") },
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
                title = "Meeting Attendance",
                subtitle = "Total Meetings Recorded",
                value = "${groupedByDate.size}",
                icon = Icons.Filled.Groups
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Meeting History", 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (groupedByDate.isEmpty()) {
                EmptyAttendanceState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groupedByDate.entries.toList().sortedByDescending { it.key }) { entry ->
                        MeetingItem(dateString = entry.key, records = entry.value)
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingItem(dateString: String, records: List<com.mahilashakti.unnati.data.model.AttendanceEntity>) {
    val presentCount = records.count { it.isPresent }
    val totalMembers = records.size
    val attendanceRate = if (totalMembers > 0) (presentCount.toFloat() / totalMembers) else 0f
    
    // Parse the ISO date string to a nicer format
    val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputSdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
    val formattedDate = try {
        val date = inputSdf.parse(dateString)
        outputSdf.format(date!!)
    } catch (e: Exception) {
        dateString
    }

    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            ) {
                Icon(
                    imageVector = Icons.Filled.EventAvailable,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedDate, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = attendanceRate,
                        modifier = Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (attendanceRate > 0.8f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$presentCount / $totalMembers present",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun EmptyAttendanceState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.EventBusy, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No meetings recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

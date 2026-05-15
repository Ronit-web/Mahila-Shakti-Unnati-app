package com.mahilashakti.unnati.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.NotificationViewModel
import com.mahilashakti.unnati.utils.SecurityUtils
import com.mahilashakti.unnati.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NotificationViewModel,
    securityUtils: SecurityUtils,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                .padding(16.dp)
        ) {
            SettingsSection(title = "Notifications") {
                SettingsToggleRow(
                    icon = Icons.Filled.Groups,
                    title = "Meeting Reminders",
                    subtitle = "SHG weekly meetings",
                    enabled = settings.isMeetingReminderEnabled,
                    onToggle = { viewModel.updateMeetingReminder(it) }
                )
                SettingsToggleRow(
                    icon = Icons.Filled.AccountBalanceWallet,
                    title = "Savings Alerts",
                    subtitle = "Upcoming deposit deadlines",
                    enabled = settings.isSavingsReminderEnabled,
                    onToggle = { viewModel.updateSavingsReminder(it) }
                )
                SettingsToggleRow(
                    icon = Icons.Filled.NotificationsActive,
                    title = "Loan EMI Alerts",
                    subtitle = "Repayment reminders",
                    enabled = settings.isLoanReminderEnabled,
                    onToggle = { /* Logic for loan reminders */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Security & Privacy") {
                var biometricEnabled by remember { mutableStateOf(securityUtils.isBiometricEnabled()) }
                SettingsToggleRow(
                    icon = Icons.Filled.Fingerprint,
                    title = "Biometric Login",
                    subtitle = "Unlock with Fingerprint/Face ID",
                    enabled = biometricEnabled,
                    onToggle = { 
                        biometricEnabled = it
                        securityUtils.setBiometricEnabled(it)
                    }
                )
                SettingsRow(
                    icon = Icons.Filled.Lock,
                    title = "Change Password",
                    onClick = { /* Navigate to change password */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "About") {
                SettingsRow(
                    icon = Icons.Filled.Info,
                    title = "App Version",
                    subtitle = "1.0.0 (Stable)"
                )
                SettingsRow(
                    icon = Icons.Filled.Help,
                    title = "Help & Support"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = "Logout",
                onClick = {
                    authViewModel.logout()
                    onNavigateToLogin()
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Logout
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

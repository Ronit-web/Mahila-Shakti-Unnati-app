package com.mahilashakti.unnati.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.Role
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.AuthState
import com.mahilashakti.unnati.viewmodel.AuthViewModel

@Composable
fun RoleSelectionScreen(
    viewModel: AuthViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val selectedRole by viewModel.selectedRole.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onNavigateToDashboard()
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Your Role",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        Role.values().forEach { role ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedRole == role,
                    onClick = { viewModel.selectedRole.value = role }
                )
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Complete Registration",
            onClick = { viewModel.register() },
            isLoading = authState is AuthState.Loading
        )
    }
}

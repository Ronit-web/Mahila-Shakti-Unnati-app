package com.mahilashakti.unnati.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.ui.components.CustomTextField
import com.mahilashakti.unnati.ui.components.PasswordTextField
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToRoleSelection: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = "Email Address"
        )
        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = "Password"
        )
        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = { viewModel.confirmPassword.value = it },
            label = "Confirm Password"
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Continue",
            onClick = { onNavigateToRoleSelection() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text("Already have an account? ")
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

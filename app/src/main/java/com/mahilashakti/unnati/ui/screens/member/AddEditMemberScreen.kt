package com.mahilashakti.unnati.ui.screens.member

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.ui.components.CustomTextField
import com.mahilashakti.unnati.ui.components.PrimaryButton
import com.mahilashakti.unnati.viewmodel.MemberUIState
import com.mahilashakti.unnati.viewmodel.MemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberScreen(
    memberId: String?,
    viewModel: MemberViewModel,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.membersList.collectAsState()
    val existingMember = members.find { it.id == memberId }

    var name by remember { mutableStateOf(existingMember?.name ?: "") }
    var phone by remember { mutableStateOf(existingMember?.phone ?: "") }
    var address by remember { mutableStateOf(existingMember?.address ?: "") }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is MemberUIState.Success -> {
                viewModel.resetState()
                onNavigateBack()
            }
            is MemberUIState.Error -> {
                Toast.makeText(context, (uiState as MemberUIState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (memberId == null) "Add Member" else "Edit Member") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name"
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address"
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Save",
                onClick = {
                    if (existingMember != null) {
                        viewModel.updateMember(existingMember.copy(name = name, phone = phone, address = address))
                    } else {
                        viewModel.addMember(name = name, phone = phone, address = address, photoUri = null)
                    }
                },
                isLoading = uiState is MemberUIState.Loading
            )
        }
    }
}

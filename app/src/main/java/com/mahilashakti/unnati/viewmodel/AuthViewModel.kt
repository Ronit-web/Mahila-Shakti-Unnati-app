package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mahilashakti.unnati.data.model.Role
import com.mahilashakti.unnati.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(userRepository.getCurrentUser())
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userRole = MutableStateFlow<Role>(Role.Member)
    val userRole: StateFlow<Role> = _userRole.asStateFlow()

    // Form states
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")
    var selectedRole = MutableStateFlow(Role.Member)

    init {
        checkSession()
    }

    private fun checkSession() {
        if (userRepository.isUserLoggedIn()) {
            _authState.value = AuthState.Success
        }
    }

    fun login() {
        if (!validateLoginForm()) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.loginWithEmail(email.value, password.value)
            if (result.isSuccess) {
                _currentUser.value = userRepository.getCurrentUser()
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register() {
        if (!validateRegisterForm()) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.registerWithEmail(email.value, password.value, selectedRole.value)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.loginWithGoogle()
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Google Sign-In failed")
            }
        }
    }

    fun resetPassword() {
        if (email.value.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.resetPassword(email.value)
            if (result.isSuccess) {
                _authState.value = AuthState.Error("Password reset link sent to email") // Using Error state for toast msg temporarily
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset link")
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
        email.value = ""
        password.value = ""
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun validateLoginForm(): Boolean {
        if (email.value.isBlank() || password.value.isBlank()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return false
        }
        return true
    }

    private fun validateRegisterForm(): Boolean {
        if (email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return false
        }
        if (password.value != confirmPassword.value) {
            _authState.value = AuthState.Error("Passwords do not match")
            return false
        }
        if (password.value.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return false
        }
        return true
    }
}

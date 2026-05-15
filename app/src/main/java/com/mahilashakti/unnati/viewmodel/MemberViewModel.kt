package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.repository.MemberRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class MemberUIState {
    object Idle : MemberUIState()
    object Loading : MemberUIState()
    object Success : MemberUIState()
    data class Error(val message: String) : MemberUIState()
}

class MemberViewModel(private val repository: MemberRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val membersList: StateFlow<List<MemberEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllMembers()
            } else {
                repository.searchMembers(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<MemberUIState>(MemberUIState.Idle)
    val uiState: StateFlow<MemberUIState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addMember(name: String, phone: String, address: String, photoUri: String?) {
        if (name.isBlank() || phone.isBlank()) {
            _uiState.value = MemberUIState.Error("Name and Phone are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = MemberUIState.Loading
            try {
                val newMember = MemberEntity(
                    name = name,
                    phone = phone,
                    address = address,
                    profilePhotoUri = photoUri
                )
                repository.insertMember(newMember)
                _uiState.value = MemberUIState.Success
            } catch (e: Exception) {
                _uiState.value = MemberUIState.Error(e.message ?: "Failed to add member")
            }
        }
    }

    fun updateMember(member: MemberEntity) {
        if (member.name.isBlank() || member.phone.isBlank()) {
            _uiState.value = MemberUIState.Error("Name and Phone are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = MemberUIState.Loading
            try {
                repository.updateMember(member)
                _uiState.value = MemberUIState.Success
            } catch (e: Exception) {
                _uiState.value = MemberUIState.Error(e.message ?: "Failed to update member")
            }
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            _uiState.value = MemberUIState.Loading
            try {
                repository.deleteMember(member)
                _uiState.value = MemberUIState.Success
            } catch (e: Exception) {
                _uiState.value = MemberUIState.Error(e.message ?: "Failed to delete member")
            }
        }
    }

    fun resetState() {
        _uiState.value = MemberUIState.Idle
    }
}

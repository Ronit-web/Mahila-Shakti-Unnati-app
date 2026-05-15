package com.mahilashakti.unnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahilashakti.unnati.repository.AiRepository
import com.mahilashakti.unnati.utils.VoiceToTextParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

data class RiskResult(val level: String, val reasoning: String)

class AiViewModel(
    private val aiRepository: AiRepository,
    private val voiceParser: VoiceToTextParser
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _riskResult = MutableStateFlow<RiskResult?>(null)
    val riskResult: StateFlow<RiskResult?> = _riskResult.asStateFlow()

    val voiceState = voiceParser.state

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        _chatHistory.value += ChatMessage(text, true)
        
        viewModelScope.launch {
            _isLoading.value = true
            val response = aiRepository.getChatResponse(text)
            _chatHistory.value += ChatMessage(response, false)
            _isLoading.value = false
        }
    }

    fun assessRisk(memberId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val rawJson = aiRepository.getMemberRiskAssessment(memberId)
            // Simplified parsing for demo
            val level = if (rawJson.contains("High", true)) "High" else if (rawJson.contains("Medium", true)) "Medium" else "Low"
            _riskResult.value = RiskResult(level, rawJson)
            _isLoading.value = false
        }
    }

    fun startVoiceInput(langCode: String) {
        voiceParser.startListening(langCode)
    }

    fun stopVoiceInput() {
        voiceParser.stopListening()
    }
}

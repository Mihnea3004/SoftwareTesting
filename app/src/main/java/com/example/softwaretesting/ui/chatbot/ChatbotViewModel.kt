package com.example.softwaretesting.ui.chatbot
import com.google.ai.client.generativeai.GenerativeModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatbotViewModel : ViewModel() {

    private val _chatMessages = MutableLiveData<List<Pair<String, String>>>()
    val chatMessages: LiveData<List<Pair<String, String>>> get() = _chatMessages

    private val apiKey = "AIzaSyCrDdN8gKDNlVBpmx2Ok5C8Ajs18XgNoGg"

    private var messageHistory = mutableListOf<Pair<String, String>>()

    init {
        _chatMessages.value = messageHistory
    }

    fun sendMessage(userMessage: String) {

        messageHistory.add("User" to userMessage)
        _chatMessages.value = messageHistory

        val generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey)
        viewModelScope.launch(Dispatchers.IO) {
            val response = generativeModel.generateContent(userMessage)
            if (response != null) {
                messageHistory.add("Gemini" to response.text.toString())
                withContext(Dispatchers.Main) {
                    _chatMessages.value = messageHistory
                }
            } else {
                withContext(Dispatchers.Main) {
                    messageHistory.add("Gemini" to "Error: Unable to get a response.")
                    _chatMessages.value = messageHistory
                }
            }
        }
    }

}
package com.example.softwaretesting.ui.chatbot
import com.google.ai.client.generativeai.GenerativeModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ChatbotViewModel : ViewModel() {

    private val _chatMessages = MutableLiveData<List<Pair<String, String>>>()
    val chatMessages: LiveData<List<Pair<String, String>>> get() = _chatMessages

    private val apiKey = "" // Replace with your actual Gemini API key

    private var messageHistory = mutableListOf<Pair<String, String>>() // Pair<Sender, Message>

    init {
        _chatMessages.value = messageHistory
    }

    fun sendMessage(userMessage: String) {
        // Add the user's message to the history
        messageHistory.add("User" to userMessage)
        _chatMessages.value = messageHistory

        // Call Gemini API on a background thread
        val generativeModel =
            GenerativeModel(
                // Specify a Gemini model appropriate for your use case
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = apiKey)
        viewModelScope.launch(Dispatchers.IO) {
            val response = generativeModel.generateContent(userMessage)
            if (response != null) {
                messageHistory.add("Gemini" to response.text.toString())
                // Switch back to the main thread to update LiveData
                withContext(Dispatchers.Main) {
                    _chatMessages.value = messageHistory
                }
            } else {
                // Handle the error case in the UI
                withContext(Dispatchers.Main) {
                    messageHistory.add("Gemini" to "Error: Unable to get a response.")
                    _chatMessages.value = messageHistory
                }
            }
        }
    }

}
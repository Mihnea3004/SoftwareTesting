package com.example.softwaretesting.ui.chatbot

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

    private val apiKey = ""
    private val client = OkHttpClient()

    private var messageHistory = mutableListOf<Pair<String, String>>() // Pair<Sender, Message>

    init {
        _chatMessages.value = messageHistory
    }

    fun sendMessage(userMessage: String) {
        // Add the user's message to the history
        messageHistory.add("User" to userMessage)
        _chatMessages.value = messageHistory

        // Call ChatGPT API on a background thread
        viewModelScope.launch(Dispatchers.IO) {
            val response = getChatGPTResponse(userMessage)
            if (response != null) {
                messageHistory.add("ChatGPT" to response)
                // Switch back to the main thread to update LiveData
                withContext(Dispatchers.Main) {
                    _chatMessages.value = messageHistory
                }
            }
        }
    }

    private suspend fun getChatGPTResponse(userMessage: String): String? {
        val json = JSONObject().apply {
            put("model", "text-davinci-003")
            put("prompt", buildPrompt(userMessage))
            put("temperature", 0.7)
            put("max_tokens", 150)
        }

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json.toString()
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val responseJson = JSONObject(response.body?.string() ?: "")
                responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text")
                    .trim()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun buildPrompt(userMessage: String): String {
        val history = messageHistory.joinToString("\n") { (sender, message) ->
            "$sender: $message"
        }
        return "$history\nUser: $userMessage\nChatGPT:"
    }
}

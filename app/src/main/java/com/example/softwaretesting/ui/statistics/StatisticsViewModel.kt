package com.example.softwaretesting.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StatisticsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _data = MutableLiveData<Map<Date, Pair<Double, Double>>>()
    val data: LiveData<Map<Date, Pair<Double, Double>>> = _data

    fun fetchDataForPeriod(period: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val startDate = when (period.lowercase()) {
            "days" -> getStartDate(Calendar.DAY_OF_YEAR, -1)
            "weeks" -> getStartDate(Calendar.WEEK_OF_YEAR, -1)
            "months" -> getStartDate(Calendar.MONTH, -1)
            "years" -> getStartDate(Calendar.YEAR, -1)
            else -> null
        } ?: return

        firestore.collection("users").document(userId).collection("financeEntries")
            .whereGreaterThanOrEqualTo("timestamp", startDate.time)
            .get()
            .addOnSuccessListener { documents ->
                val dataMap = mutableMapOf<Date, Pair<Double, Double>>()
                for (document in documents) {
                    val incomeType = document.getString("incomeType")
                    val amount = document.getDouble("amount") ?: 0.0
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val date = Date(timestamp)

                    val currentPair = dataMap[date] ?: Pair(0.0, 0.0)
                    val updatedPair = if (incomeType == "income") {
                        Pair(currentPair.first + amount, currentPair.second)
                    } else {
                        Pair(currentPair.first, currentPair.second + amount)
                    }
                    dataMap[date] = updatedPair
                }
                _data.value = dataMap
            }
    }

    private fun getStartDate(field: Int, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(field, amount)
        return calendar.time
    }
}

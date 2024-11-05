data class FinanceEntry(
    val amount: Double = 0.0,
    val incomeType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var id:String = ""
)
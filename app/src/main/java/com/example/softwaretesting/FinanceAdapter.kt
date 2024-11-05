package com.example.softwaretesting

import FinanceEntry
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceAdapter(
    private var financeList: MutableList<FinanceEntry>,
    private val context: Context
) : RecyclerView.Adapter<FinanceAdapter.FinanceViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    class FinanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false)
        return FinanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinanceViewHolder, position: Int) {
        val financeEntry = financeList[position]
        holder.amountTextView.text = "${financeEntry.amount}"
        holder.typeTextView.text = if (financeEntry.incomeType == "income") "Income" else "Expense"
        holder.timeTextView.text = dateFormat.format(Date(financeEntry.timestamp))

        holder.itemView.setOnClickListener {
            showPopupMenu(holder.itemView, financeEntry, position)
        }
    }

    override fun getItemCount(): Int = financeList.size

    private fun showPopupMenu(view: View, financeEntry: FinanceEntry, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.finance_item_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    showEditDialog(financeEntry, position)
                    true
                }
                R.id.remove -> {
                    removeEntry(financeEntry) // Only remove from Firestore, let the listener update the UI
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun removeEntry(financeEntry: FinanceEntry) {
        userId?.let {
            firestore.collection("users").document(it)
                .collection("financeEntries").document(financeEntry.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error removing entry", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showEditDialog(financeEntry: FinanceEntry, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_finance, null)
        val editTextAmount: EditText = dialogView.findViewById(R.id.dialog_amount)
        val radioIncome: RadioButton = dialogView.findViewById(R.id.dialog_radio_income)
        val radioExpense: RadioButton = dialogView.findViewById(R.id.dialog_radio_expense)

        editTextAmount.setText(financeEntry.amount.toString())
        if (financeEntry.incomeType == "income") radioIncome.isChecked = true else radioExpense.isChecked = true

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Edit Entry")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val newAmount = editTextAmount.text.toString().toDoubleOrNull()
                var IncomeType = ""
                if(radioIncome.isChecked){
                    IncomeType = "income"
                }else if(radioExpense.isChecked){
                    IncomeType = "expense"
                } else
                    Toast.makeText(context, "Please select an income type", Toast.LENGTH_SHORT).show()

                if (newAmount != null) {
                    updateEntry(financeEntry, newAmount, IncomeType)
                } else {
                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .create()
            .show()
    }

    private fun updateEntry(financeEntry: FinanceEntry, newAmount: Double, incomeType: String) {
        userId?.let {
            val updatedEntry = financeEntry.copy(amount = newAmount, incomeType = incomeType)
            firestore.collection("users").document(it)
                .collection("financeEntries").document(financeEntry.id)
                .set(updatedEntry)
                .addOnSuccessListener {
                    Toast.makeText(context, "Entry updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error updating entry", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Use DiffUtil for efficient list updates
    fun updateData(newList: List<FinanceEntry>) {
        val diffCallback = FinanceDiffCallback(financeList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        financeList.clear()
        financeList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil Callback for efficient list updates
    class FinanceDiffCallback(
        private val oldList: List<FinanceEntry>,
        private val newList: List<FinanceEntry>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

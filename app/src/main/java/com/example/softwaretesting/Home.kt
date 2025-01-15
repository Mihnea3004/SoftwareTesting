package com.example.softwaretesting

import FinanceEntry
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.softwaretesting.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_statistics, R.id.nav_chatbot
            ), drawerLayout
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> binding.appBarHome.addButton.show()
                else -> binding.appBarHome.addButton.hide()
            }
        }

        binding.appBarHome.addButton.setOnClickListener {
            showAddDialog()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_finance, null)
        val editTextAmount: EditText = dialogView.findViewById(R.id.dialog_amount)
        val radioIncome: RadioButton = dialogView.findViewById(R.id.dialog_radio_income)
        val radioExpense: RadioButton = dialogView.findViewById(R.id.dialog_radio_expense)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add Entry")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val amount = editTextAmount.text.toString().toDoubleOrNull()
                var incomeType : String = ""
                if(radioIncome.isChecked){
                    incomeType = "income"
                } else
                if(radioExpense.isChecked){
                    incomeType = "expense"
                }
                if (amount != null && incomeType != "") {
                    addEntry(amount, incomeType)
                } else {
                    Toast.makeText(this, "Please fill every field", Toast.LENGTH_SHORT).show()
                }
            }
            .create()
            .show()
    }

    fun addEntry(amount: Double, incomeType: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("users").document(userId).collection("financeEntries")

        val newDocRef = userCollection.document()
        val newFinanceEntry = FinanceEntry(
            amount = amount,
            incomeType = incomeType,
            timestamp = System.currentTimeMillis(),
            id = newDocRef.id
        )

        newDocRef.set(newFinanceEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Entry added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

package com.example.softwaretesting.ui.home

import FinanceEntry
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.softwaretesting.FinanceAdapter
import com.example.softwaretesting.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var financeEntriesListener: ListenerRegistration? = null
    private lateinit var financeAdapter: FinanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView and Adapter
        financeAdapter = FinanceAdapter(mutableListOf(), requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = financeAdapter
        binding.recyclerView.setHasFixedSize(true)

        // Set up Firestore real-time listener for finance entries
        auth.currentUser?.uid?.let { userId ->
            listenToFinanceEntries(userId)
        }

        return root
    }

    private fun listenToFinanceEntries(userId: String) {
        val userCollection = firestore.collection("users").document(userId).collection("financeEntries")

        financeEntriesListener = userCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Map Firestore documents to FinanceEntry objects
                val updatedEntries = snapshot.documents.mapNotNull { document ->
                    document.toObject(FinanceEntry::class.java)?.apply {
                        id = document.id // Ensure the document ID is set
                    }
                }

                // Update adapter data efficiently using DiffUtil
                financeAdapter.updateData(updatedEntries)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        financeEntriesListener?.remove()
        _binding = null
    }
}

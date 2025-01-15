package com.example.softwaretesting

import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class HomeTest {

    private lateinit var homeActivity: Home
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockAuth: FirebaseAuth

    @Before
    fun setUp() {
        homeActivity = spy(Home())
        mockFirestore = mock()
        mockAuth = mock()
    }

    @Test
    fun `test addEntry adds entry to Firestore`() {
        val amount = 500.0
        val incomeType = "income"

        doReturn("testUserId").whenever(mockAuth).currentUser?.uid

        homeActivity.addEntry(amount, incomeType)

        verify(mockFirestore.collection("users").document("testUserId").collection("financeEntries"))
    }

    @Test
    fun `test addButton shows add dialog`() {
        val toolbar: Toolbar = mock()

        homeActivity.showAddDialog()

        verify(toolbar).showContextMenu()
    }
}

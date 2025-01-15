package com.example.softwaretesting

import FinanceEntry
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.softwaretesting.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var fragment: HomeFragment
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockLifecycleOwner: LifecycleOwner
    private lateinit var lifecycleRegistry: LifecycleRegistry

    @Before
    fun setUp() {
        fragment = HomeFragment()
        mockFirestore = mock()
        mockAuth = mock()
        mockLifecycleOwner = mock()
        lifecycleRegistry = LifecycleRegistry(mockLifecycleOwner)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    @Test
    fun `test listenToFinanceEntries updates adapter with data`() {
        val mockQuerySnapshot: QuerySnapshot = mock()
        val mockDocumentSnapshot = mock<DocumentSnapshot>()
        whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.toObject(FinanceEntry::class.java)).thenReturn(FinanceEntry())

        fragment.listenToFinanceEntries("testUserId")

        verify(mockFirestore.collection(any())).document(any()).collection(any())
        verify(mockDocumentSnapshot).toObject(FinanceEntry::class.java)
    }

    @After
    fun tearDown() {
        fragment.onDestroyView()
    }
}

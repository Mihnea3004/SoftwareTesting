package com.example.softwaretesting

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.softwaretesting.ui.statistics.StatisticsFragment
import com.example.softwaretesting.ui.statistics.StatisticsViewModel
import com.github.mikephil.charting.data.LineData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class StatisticsFragmentTest {

    private lateinit var fragment: StatisticsFragment

    @Mock
    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        fragment = StatisticsFragment()
    }

    @Test
    fun `test updateChart sets chart data`() {
        val dataMap = mapOf(
            Date() to (1000.0 to 500.0),
            Date() to (2000.0 to 1500.0)
        )
        val liveData = MutableLiveData<Map<Date, Pair<Double, Double>>>()
        liveData.value = dataMap

        fragment.updateChart(dataMap)

        assert(fragment.lineChart.data is LineData)
    }
}

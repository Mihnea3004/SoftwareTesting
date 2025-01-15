package com.example.softwaretesting.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.softwaretesting.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    lateinit var lineChart: LineChart
    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        statisticsViewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        lineChart = binding.lineChart

        setupChart()
        setupButtons()

        statisticsViewModel.data.observe(viewLifecycleOwner) { dataMap ->
            updateChart(dataMap)
        }

        return root
    }

    private fun setupChart() {
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.description.isEnabled = false
    }

    private fun setupButtons() {
        binding.btnDays.setOnClickListener { statisticsViewModel.fetchDataForPeriod("days") }
        binding.btnWeeks.setOnClickListener { statisticsViewModel.fetchDataForPeriod("weeks") }
        binding.btnMonths.setOnClickListener { statisticsViewModel.fetchDataForPeriod("months") }
        binding.btnYears.setOnClickListener { statisticsViewModel.fetchDataForPeriod("years") }
    }

    fun updateChart(dataMap: Map<Date, Pair<Double, Double>>) {
        val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

        val incomeEntries = dataMap.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.first.toFloat())
        }
        val expenseEntries = dataMap.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.second.toFloat())
        }

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply { color = android.graphics.Color.GREEN }
        val expenseDataSet = LineDataSet(expenseEntries, "Expense").apply { color = android.graphics.Color.RED }

        lineChart.data = LineData(incomeDataSet, expenseDataSet)
        lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

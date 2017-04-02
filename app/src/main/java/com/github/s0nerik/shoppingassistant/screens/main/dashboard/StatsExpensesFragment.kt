package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import android.os.Bundle
import android.view.View
import com.github.debop.kodatimes.startOfDay
import com.github.debop.kodatimes.toDateTime
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsExpensesBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.purchases
import kotlinx.android.synthetic.main.fragment_stats_expenses.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class StatsExpensesFragment : BaseBoundFragment<FragmentStatsExpensesBinding>(R.layout.fragment_stats_expenses) {
    val purchases
        get() = purchases(realm)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChart()
    }

    private fun initChart() {
        val colors = intArrayOf(R.color.colorAccent)

        var i = 0
        val entries = purchases
                .groupBy { it.date?.toDateTime()?.startOfDay() }
                .toSortedMap(compareBy { it })
                .map {
                    BarEntry(
                            i++.toFloat(),
                            it.value.map(Purchase::priceInDefaultCurrency).sum()
                    )
                }

        val dataSet = BarDataSet(entries, null)
        dataSet.apply {
            setColors(colors, act)
            valueTextSize = 16f
        }

        chart.apply {
            data = BarData(dataSet)
            description = null

            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawLabels(false)

            axisRight.setDrawGridLines(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawLabels(false)

            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLabels(false)
        }
    }
}
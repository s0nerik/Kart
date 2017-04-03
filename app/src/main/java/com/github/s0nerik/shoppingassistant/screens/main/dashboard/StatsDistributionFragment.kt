package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.DashboardDataPeriod
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsDistributionBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemStatsDistributionBinding
import com.github.s0nerik.shoppingassistant.ext.getColor
import com.github.s0nerik.shoppingassistant.ext.limit
import com.github.s0nerik.shoppingassistant.ext.toObservable
import com.github.s0nerik.shoppingassistant.purchases
import com.github.s0nerik.shoppingassistant.screens.main.DashboardFragment
import com.google.android.flexbox.FlexboxLayoutManager
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_stats_distribution.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class StatsDistributionFragment : BaseBoundFragment<FragmentStatsDistributionBinding>(R.layout.fragment_stats_distribution) {
    val purchases
        get() = purchases(realm)

    override fun onViewCreated(view: android.view.View?, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DashboardFragment.dataPeriod
                .toObservable()
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(this::initDistributionChart)
    }

    private fun initDistributionChart(period: DashboardDataPeriod) {
        val colors = intArrayOf(
                R.color.material_color_red_500,
                R.color.material_color_pink_500,
                R.color.material_color_purple_500,
                R.color.material_color_deep_purple_500,
                R.color.material_color_indigo_500,
                R.color.material_color_blue_500,
                R.color.material_color_teal_500,
                R.color.material_color_green_500,
                R.color.material_color_orange_500,
                R.color.material_color_deep_orange_500
        )

        val entries = purchases
                .filter { it.date!! >= period.startDate }
                .groupBy { it.item?.category }
                .map { PieEntry(it.value.size.toFloat(), it.key?.name) }
                .sortedByDescending { it.value }
                .limit(10)
        val legendItems = entries.mapIndexed { i, entry -> DistributionLegendItem(getColor(colors[i % colors.size]), entry.label) }

        rvLegend.layoutManager = FlexboxLayoutManager()

        LastAdapter.with(legendItems, BR.item)
                .type { Type<ItemStatsDistributionBinding>(R.layout.item_stats_distribution) }
                .into(rvLegend)

        val dataSet = PieDataSet(entries, null)
        dataSet.apply {
            setColors(colors, act)
            sliceSpace = 4f
            valueTextColor = ContextCompat.getColor(act, R.color.material_color_white)
            valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                if (value >= 7f) "%.0f".format(value)+" %" else ""
            }
            valueTextSize = 14f
        }

        chart.apply {
            data = PieData(dataSet)
            description = null
            holeRadius = 50f
            transparentCircleRadius = 55f
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            legend.isEnabled = false

            highlightValue(0f, 0)
        }
    }
}

class DistributionLegendItem(val color: Int, val name: String)
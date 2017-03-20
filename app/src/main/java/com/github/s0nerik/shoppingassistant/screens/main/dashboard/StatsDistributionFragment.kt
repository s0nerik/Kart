package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsDistributionBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemStatsDistributionBinding
import com.github.s0nerik.shoppingassistant.ext.getColor
import com.github.s0nerik.shoppingassistant.purchases
import kotlinx.android.synthetic.main.fragment_stats_distribution.*
import org.jetbrains.anko.support.v4.act
import java.text.DecimalFormat

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
        initDistributionChart()
    }

    private fun initDistributionChart() {
        val colors = intArrayOf(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)

        val entries = purchases.groupBy { it.item?.category }.map { PieEntry(it.value.size.toFloat(), it.key?.name) }
        val legendItems = entries.mapIndexed { i, entry -> DistributionLegendItem(getColor(colors[i % colors.size]), entry.label) }

        LastAdapter.with(legendItems, BR.item)
                .type { Type<ItemStatsDistributionBinding>(R.layout.item_stats_distribution) }
                .into(rvLegend)

        val dataSet = PieDataSet(entries, null)
        dataSet.apply {
            setColors(colors, act)
            sliceSpace = 4f
            valueTextColor = ContextCompat.getColor(act, R.color.material_color_white)
            valueFormatter = PercentFormatter(DecimalFormat("##"))
            valueTextSize = 14f
        }

        chart.apply {
            data = PieData(dataSet)
            description = null
            holeRadius = 10f
            transparentCircleRadius = 20f
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            legend.isEnabled = false
        }
    }
}

class DistributionLegendItem(val color: Int, val name: String)
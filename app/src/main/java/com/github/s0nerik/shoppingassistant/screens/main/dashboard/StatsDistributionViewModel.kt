package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.nitrico.lastadapter.BR
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemStatsDistributionBinding
import com.github.s0nerik.shoppingassistant.ext.getColor
import com.github.s0nerik.shoppingassistant.ext.limit
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.repositories.stats.IStatsRepository
import com.github.s0nerik.shoppingassistant.repositories.stats.StatsRepository
import com.github.s0nerik.shoppingassistant.screens.main.MainViewModel
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * Created by Alex on 11/22/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/s0nerik
 */
class StatsDistributionViewModel : BaseViewModel() {

    private lateinit var mainVm: MainViewModel
    private lateinit var statsRepo: IStatsRepository

    val legendItems = observableListOf<DistributionLegendItem>()

    fun init(mainVm: MainViewModel, statsRepo: IStatsRepository = StatsRepository) {
        this.mainVm = mainVm
        this.statsRepo = statsRepo
    }

    fun initDistributionChart(rvLegend: RecyclerView, chart: PieChart) {

        mainVm.observeDataPeriodChanges()
                .flatMapSingle { statsRepo.getPurchaseCategoryDistribution(it.startDate) }
                .map {
                    val pieEntries = it.map { PieEntry(it.value.size.toFloat(), it.key?.name) }
                            .sortedByDescending { it.value }
                            .limit(10)

                    val legendItems = pieEntries.mapIndexed { i, it -> DistributionLegendItem(getColor(ITEM_COLORS[i % ITEM_COLORS.size]), it.label) }

                    pieEntries to legendItems
                }
                .subscribe {
                    updateChartData(chart, it.first)
                    legendItems += it.second
                }

        rvLegend.layoutManager = FlexboxLayoutManager(rvLegend.context)

        LastAdapter(legendItems, BR.item)
                .type { _, _ -> Type<ItemStatsDistributionBinding>(R.layout.item_stats_distribution) }
                .into(rvLegend)
    }

    private fun updateChartData(chart: PieChart, entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, null)
        dataSet.apply {
            setColors(ITEM_COLORS, chart.context)
            sliceSpace = 4f
            valueTextColor = ContextCompat.getColor(chart.context, R.color.material_color_white)
            valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                if (value >= 7f) "%.0f".format(value) + " %" else ""
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

    companion object {
        private val ITEM_COLORS = intArrayOf(
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
    }
}
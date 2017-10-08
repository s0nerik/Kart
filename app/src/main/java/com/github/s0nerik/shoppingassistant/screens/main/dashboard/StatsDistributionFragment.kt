package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsDistributionBinding

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class StatsDistributionViewModel(

)

class StatsDistributionFragment : BaseBoundFragment<FragmentStatsDistributionBinding>(R.layout.fragment_stats_distribution) {
//    override fun onViewCreated(view: android.view.View?, savedInstanceState: android.os.Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initDistributionChart(DashboardFragment.vm.dataPeriod)
//        DashboardFragment.vm.observeChanges(BR.dataPeriod)
//                .bindUntilEvent(this, FragmentEvent.DESTROY)
//                .subscribe { initDistributionChart(DashboardFragment.vm.dataPeriod) }
//    }
//
//    private fun initDistributionChart(period: DashboardDataPeriod) {
//        val colors = intArrayOf(
//                R.color.material_color_red_500,
//                R.color.material_color_pink_500,
//                R.color.material_color_purple_500,
//                R.color.material_color_deep_purple_500,
//                R.color.material_color_indigo_500,
//                R.color.material_color_blue_500,
//                R.color.material_color_teal_500,
//                R.color.material_color_green_500,
//                R.color.material_color_orange_500,
//                R.color.material_color_deep_orange_500
//        )
//
//        val entries = Db.statsDistribution(realm, period.startDate)
//                .map { PieEntry(it.value.size.toFloat(), it.key?.name) }
//                .sortedByDescending { it.value }
//                .limit(10)
//
//        if (entries.isEmpty()) return
//
//        val legendItems = entries.mapIndexed { i, entry -> DistributionLegendItem(getColor(colors[i % colors.size]), entry.label) }
//
//        rvLegend.layoutManager = FlexboxLayoutManager()
//
//        LastAdapter(legendItems, BR.item)
//                .type { _, _ -> Type<ItemStatsDistributionBinding>(R.layout.item_stats_distribution) }
//                .into(rvLegend)
//
//        val dataSet = PieDataSet(entries, null)
//        dataSet.apply {
//            setColors(colors, act)
//            sliceSpace = 4f
//            valueTextColor = ContextCompat.getColor(act, R.color.material_color_white)
//            valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
//                if (value >= 7f) "%.0f".format(value)+" %" else ""
//            }
//            valueTextSize = 14f
//        }
//
//        chart.apply {
//            data = PieData(dataSet)
//            description = null
//            holeRadius = 50f
//            transparentCircleRadius = 55f
//            setDrawEntryLabels(false)
//            setUsePercentValues(true)
//            legend.isEnabled = false
//
//            highlightValue(0f, 0)
//        }
//    }
}

class DistributionLegendItem(val color: Int, val name: String)
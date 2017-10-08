package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsExpensesBinding
import io.realm.Realm

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class ExpensesViewModel(
        private val realm: Realm
) {
//    val purchases
//        get() = Db.purchases(realm)
}

class StatsExpensesFragment : BaseBoundFragment<FragmentStatsExpensesBinding>(R.layout.fragment_stats_expenses) {
    private lateinit var vm : ExpensesViewModel

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        vm = ExpensesViewModel(realm)
//    }
//
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initChart()
//    }
//
//    private fun initChart() {
//        val colors = intArrayOf(R.color.colorAccent)
//
//        var i = 0
//        val entries = vm.purchases
//                .groupBy { it.date?.toDateTime()?.startOfDay() }
//                .toSortedMap(compareBy { it })
//                .map {
//                    BarEntry(
//                            i++.toFloat(),
//                            it.value.map(RealmPurchase::priceInDefaultCurrency).sum()
//                    )
//                }
//
//        val dataSet = BarDataSet(entries, null)
//        dataSet.apply {
//            setColors(colors, act)
//            valueTextSize = 16f
//        }
//
//        chart.apply {
//            data = BarData(dataSet)
//            description = null
//
//            axisLeft.setDrawGridLines(false)
//            axisLeft.setDrawAxisLine(false)
//            axisLeft.setDrawLabels(false)
//
//            axisRight.setDrawGridLines(false)
//            axisRight.setDrawAxisLine(false)
//            axisRight.setDrawLabels(false)
//
//            xAxis.setDrawGridLines(false)
//            xAxis.setDrawAxisLine(false)
//            xAxis.setDrawLabels(false)
//        }
//    }
}
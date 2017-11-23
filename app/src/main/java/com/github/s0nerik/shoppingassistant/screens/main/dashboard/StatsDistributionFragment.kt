package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsDistributionBinding
import kotlinx.android.synthetic.main.fragment_stats_distribution.*

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class StatsDistributionFragment : BaseBoundVmFragment<FragmentStatsDistributionBinding, StatsDistributionViewModel>(
        R.layout.fragment_stats_distribution, StatsDistributionViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(getActivityViewModel())
        vm.initDistributionChart(rvLegend, chart)
    }
}

class DistributionLegendItem(val color: Int, val name: String)
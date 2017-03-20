package com.github.s0nerik.shoppingassistant.screens.main.dashboard

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentStatsExpensesBinding
import com.github.s0nerik.shoppingassistant.purchases

/**
 * Created by Alex on 3/19/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class StatsExpensesFragment : BaseBoundFragment<FragmentStatsExpensesBinding>(R.layout.fragment_stats_expenses) {
    val purchases
        get() = purchases(realm)
}
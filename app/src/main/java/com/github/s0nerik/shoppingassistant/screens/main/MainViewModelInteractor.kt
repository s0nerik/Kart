package com.github.s0nerik.shoppingassistant.screens.main

import com.github.s0nerik.shoppingassistant.DashboardDataPeriod
import io.reactivex.Maybe


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface MainViewModelInteractor {
    fun display(state: State)
    fun selectDashboardPeriod(): Maybe<DashboardDataPeriod>
}
package com.github.s0nerik.shoppingassistant.screens.main

import android.databinding.Bindable
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.DashboardDataPeriod
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.utils.weak

class MainViewModel : BaseViewModel() {
    private var interactor by weak<MainViewModelInteractor>()

    var dashboardDataPeriod: DashboardDataPeriod = DashboardDataPeriod.from(MainPrefs.expensesLimitPeriod)
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.dashboardDataPeriod)
        }

    var state: State? = null
        @Bindable("dashboardDataPeriod") get
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
            value?.let { interactor?.display(value) }
        }

    fun init(interactor: MainViewModelInteractor) {
        this.interactor = interactor
        state = State.DASHBOARD
    }

    fun observeDataPeriodChanges() = observePropertyChanges(BR.dashboardDataPeriod).map { it.dashboardDataPeriod }

    fun selectDashboardPeriod() {
        interactor?.let {
            it.selectDashboardPeriod().subscribe {
                dashboardDataPeriod = it
            }
        }
    }
}
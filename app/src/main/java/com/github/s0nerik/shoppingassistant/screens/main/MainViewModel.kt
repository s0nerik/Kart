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
        private set

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
        TODO()
//        val menu = PopupMenu(activity, activity.btnPeriod, Gravity.TOP.or(Gravity.RIGHT), 0, R.style.Base_Widget_AppCompat_PopupMenu_Overflow)
//        menu.inflate(R.menu.dashboard_popup_period)
//        menu.setOnMenuItemClickListener {
//            val period = when (it.itemId) {
//                R.id.today -> DashboardDataPeriod.TODAY
//                R.id.yesterday -> DashboardDataPeriod.YESTERDAY
//                R.id.last_week -> DashboardDataPeriod.LAST_WEEK
//                R.id.last_month -> DashboardDataPeriod.LAST_MONTH
//                R.id.six_months -> DashboardDataPeriod.SIX_MONTHS
//                R.id.last_year -> DashboardDataPeriod.LAST_YEAR
//                else -> throw IllegalArgumentException()
//            }
//            DashboardFragment.vm.dataPeriod = period
//            true
//        }
//        menu.show()
    }
}
package com.github.s0nerik.shoppingassistant.screens.main

import android.databinding.ObservableField
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.MenuItem
import com.github.s0nerik.shoppingassistant.DashboardDataPeriod
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.screens.main.fragments.CartFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.DashboardFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.PurchaseListsFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.history.HistoryFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityViewModel(private val activity: MainActivity) {
    private val dashboardFragment by lazy { DashboardFragment() }
    private val historyFragment by lazy { HistoryFragment() }
    private val listsFragment by lazy { PurchaseListsFragment() }
    private val cartFragment by lazy { CartFragment() }

    val currentFragment = ObservableField<Fragment>(dashboardFragment)

    init {
        activity.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
        onNavigationItemSelected(R.id.dashboard)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        return onNavigationItemSelected(item.itemId)
    }

    private fun onNavigationItemSelected(@IdRes id: Int): Boolean {
        val selectedFragment = when(id) {
            R.id.dashboard -> dashboardFragment
            R.id.history -> historyFragment
            R.id.lists -> listsFragment
            R.id.cart -> cartFragment
            else -> dashboardFragment
        }
        currentFragment.set(selectedFragment)

        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, selectedFragment)
            .commit()
        return true
    }

    fun selectDashboardPeriod() {
        val menu = PopupMenu(activity, activity.btnPeriod, Gravity.TOP.or(Gravity.RIGHT), 0, R.style.Base_Widget_AppCompat_PopupMenu_Overflow)
        menu.inflate(R.menu.dashboard_popup_period)
        menu.setOnMenuItemClickListener {
            val period = when (it.itemId) {
                R.id.today -> DashboardDataPeriod.TODAY
                R.id.yesterday -> DashboardDataPeriod.YESTERDAY
                R.id.last_week -> DashboardDataPeriod.LAST_WEEK
                R.id.last_month -> DashboardDataPeriod.LAST_MONTH
                R.id.six_months -> DashboardDataPeriod.SIX_MONTHS
                R.id.last_year -> DashboardDataPeriod.LAST_YEAR
                else -> throw IllegalArgumentException()
            }
            DashboardFragment.vm.dataPeriod = period
            true
        }
        menu.show()
    }
}
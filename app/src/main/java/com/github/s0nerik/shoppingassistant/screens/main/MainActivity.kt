package com.github.s0nerik.shoppingassistant.screens.main

import android.databinding.ObservableList
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import berlin.volders.badger.BadgeShape
import berlin.volders.badger.Badger
import berlin.volders.badger.CountBadge
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.currentCart
import com.github.s0nerik.shoppingassistant.databinding.ActivityMainBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityViewModel(private val activity: MainActivity) {
    val dashboardFragment by lazy { DashboardFragment() }
    val historyFragment by lazy { HistoryFragment() }
    val cartFragment by lazy { CartFragment() }

    init {
        activity.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, when(item.itemId) {
                    R.id.dashboard -> dashboardFragment
                    R.id.history -> historyFragment
                    R.id.cart -> cartFragment
                    else -> dashboardFragment
                })
                .commit()
        return true
    }
}

class MainActivity : BaseBoundActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var badge: CountBadge

    // TODO: clear all listeners of the list in cart when cart gets saved.
    private val cartChangeListener = object: ObservableList.OnListChangedCallback<ObservableList<Purchase>>() {
        override fun onItemRangeMoved(p0: ObservableList<Purchase>, p1: Int, p2: Int, p3: Int) = badge.setCount(p0.size)
        override fun onItemRangeChanged(p0: ObservableList<Purchase>, p1: Int, p2: Int) = badge.setCount(p0.size)
        override fun onChanged(p0: ObservableList<Purchase>) = badge.setCount(p0.size)
        override fun onItemRangeInserted(p0: ObservableList<Purchase>, p1: Int, p2: Int) = badge.setCount(p0.size)
        override fun onItemRangeRemoved(p0: ObservableList<Purchase>, p1: Int, p2: Int) = badge.setCount(p0.size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = MainActivityViewModel(this)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, binding.vm.dashboardFragment)
                .commit()

        badge = Badger.sett(binding.bottomNavigation.menu.getItem(2), CountBadge.Factory(this, BadgeShape.circle(0.5f, Gravity.RIGHT.or(Gravity.TOP))))
        currentCart.purchasesObservableList.addOnListChangedCallback(cartChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCart.purchasesObservableList.removeOnListChangedCallback(cartChangeListener)
    }
}

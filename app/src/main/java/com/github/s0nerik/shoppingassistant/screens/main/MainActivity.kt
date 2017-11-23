package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.Gravity
import berlin.volders.badger.BadgeShape
import berlin.volders.badger.Badger
import berlin.volders.badger.CountBadge
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityMainBinding
import com.github.s0nerik.shoppingassistant.model.RealmPurchase
import com.github.s0nerik.shoppingassistant.screens.main.cart.CartFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.DashboardFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.history.HistoryFragment
import com.github.s0nerik.shoppingassistant.screens.main.purchase_lists.PurchaseListsFragment
import com.github.s0nerik.shoppingassistant.screens.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseBoundVmActivity<ActivityMainBinding, MainViewModel>(
        R.layout.activity_main, MainViewModel::class
), MainViewModelInteractor {
    private val fragments = mapOf(
            State.DASHBOARD to DashboardFragment(),
            State.HISTORY to HistoryFragment(),
            State.PURCHASE_LIST to PurchaseListsFragment(),
            State.CART to CartFragment()
    )

    private lateinit var badge: CountBadge

    private fun onCartChanged(cart: List<RealmPurchase>) {
        badge.count = cart.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.interactor = this

        toolbar.setOnMenuItemClickListener {
            if (it!!.itemId == R.id.settings)
                startActivity<SettingsActivity>()
            true
        }

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            vm.state = State.values().first { it.menuId == menuItem.itemId }
            true
        }

        badge = Badger.sett(binding.bottomNavigation.menu.getItem(3), CountBadge.Factory(this, BadgeShape.circle(0.5f, Gravity.RIGHT.or(Gravity.TOP))))
        // TODO: add cart changes listener
    }

    override fun display(state: State) {
        fragments[state]!!.replaceAndCommit(R.id.container)
    }
}
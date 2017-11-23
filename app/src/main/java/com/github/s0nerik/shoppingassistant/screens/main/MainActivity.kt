package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import berlin.volders.badger.BadgeShape
import berlin.volders.badger.Badger
import berlin.volders.badger.CountBadge
import com.github.s0nerik.shoppingassistant.DashboardDataPeriod
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityMainBinding
import com.github.s0nerik.shoppingassistant.model.RealmPurchase
import com.github.s0nerik.shoppingassistant.screens.main.fragments.CartFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.DashboardFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.PurchaseListsFragment
import com.github.s0nerik.shoppingassistant.screens.main.fragments.history.HistoryFragment
import com.github.s0nerik.shoppingassistant.screens.settings.SettingsActivity
import io.reactivex.Maybe
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
        vm.init(this)

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

    override fun selectDashboardPeriod(): Maybe<DashboardDataPeriod> {
        return Maybe.create { sub ->
            val menu = PopupMenu(this, btnPeriod, Gravity.TOP.or(Gravity.RIGHT), 0, R.style.Base_Widget_AppCompat_PopupMenu_Overflow)
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
                sub.onSuccess(period)
                true
            }
            menu.show()
            sub.setCancellable { menu.dismiss() }
        }
    }
}
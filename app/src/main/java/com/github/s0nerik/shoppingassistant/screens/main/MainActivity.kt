package com.github.s0nerik.shoppingassistant.screens.main

import android.databinding.ObservableList
import android.os.Bundle
import android.view.Gravity
import berlin.volders.badger.BadgeShape
import berlin.volders.badger.Badger
import berlin.volders.badger.CountBadge
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityMainBinding
import com.github.s0nerik.shoppingassistant.model.RealmCart
import com.github.s0nerik.shoppingassistant.model.RealmPurchase
import com.github.s0nerik.shoppingassistant.screens.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseBoundActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var badge: CountBadge

    // TODO: clear all listeners of the list in cart when cart gets saved.
    private val cartChangeListener = object : ObservableList.OnListChangedCallback<ObservableList<RealmPurchase>>() {
        override fun onItemRangeMoved(p0: ObservableList<RealmPurchase>, p1: Int, p2: Int, p3: Int) = onCartChanged(p0)
        override fun onItemRangeChanged(p0: ObservableList<RealmPurchase>, p1: Int, p2: Int) = onCartChanged(p0)
        override fun onChanged(p0: ObservableList<RealmPurchase>) = onCartChanged(p0)
        override fun onItemRangeInserted(p0: ObservableList<RealmPurchase>, p1: Int, p2: Int) = onCartChanged(p0)
        override fun onItemRangeRemoved(p0: ObservableList<RealmPurchase>, p1: Int, p2: Int) = onCartChanged(p0)
    }

    private fun onCartChanged(cart: List<RealmPurchase>) {
        badge.count = cart.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = MainActivityViewModel(this)

        toolbar.setOnMenuItemClickListener {
            if (it!!.itemId == R.id.settings) {
                startActivity<SettingsActivity>()
            }
            true
        }

        badge = Badger.sett(binding.bottomNavigation.menu.getItem(3), CountBadge.Factory(this, BadgeShape.circle(0.5f, Gravity.RIGHT.or(Gravity.TOP))))
        RealmCart.purchases.addOnListChangedCallback(cartChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        RealmCart.purchases.removeOnListChangedCallback(cartChangeListener)
    }
}

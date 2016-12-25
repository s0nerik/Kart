package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val purchases = realm.where(Purchase::class.java).findAll()
        LastAdapter.with(purchases, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_recent_purchases)
                .into(recentsRecycler)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
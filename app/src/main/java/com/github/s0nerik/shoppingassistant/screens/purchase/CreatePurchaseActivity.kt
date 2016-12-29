package com.github.s0nerik.shoppingassistant.screens.purchase

import android.databinding.ObservableBoolean
import android.os.Bundle
import com.github.ajalt.timberkt.d
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
import com.github.s0nerik.shoppingassistant.favoritePurchases
import com.github.s0nerik.shoppingassistant.frequentPurchases
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import io.realm.PurchaseRealmProxy
import kotlinx.android.synthetic.main.activity_create_purchase.*

class CreatePurchaseViewModel(private val activity: CreatePurchaseActivity) {
    val isSearching: ObservableBoolean = ObservableBoolean(false)

    init {
        activity.apply {
            etSearch.textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe {
                        d { "${etSearch.text}" }
                        isSearching.set(etSearch.text.isNotEmpty())
                    }
        }
    }
}

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = CreatePurchaseViewModel(this)
        initFavorites()
        initFrequents()
    }

    private fun initFavorites() {
        val favs = favoritePurchases(realm)
        LastAdapter.with(favs, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase_horizontal)
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        val frequents = frequentPurchases(realm)
        LastAdapter.with(frequents, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }
}

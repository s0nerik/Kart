package com.github.s0nerik.shoppingassistant.screens.purchase

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.os.Bundle
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_create_purchase.*

class CreatePurchaseViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm
) {
    val isSearching: ObservableBoolean = ObservableBoolean(false)

    val frequents: RealmResults<Purchase> by lazy { frequentPurchases(realm) }
    val favorites: RealmResults<Purchase> by lazy { favoritePurchases(realm) }

    private val purchases: RealmResults<Purchase> by lazy { purchases(realm) }
    val filteredSearchResults: ObservableArrayList<Purchase> = ObservableArrayList()

    init {
        activity.apply {
            etSearch.textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe { s ->
                        isSearching.set(s.isNotEmpty())

                        filteredSearchResults.clear()
                        filteredSearchResults += purchases.filter { it.readableName.contains(s, true) }
                    }
        }
    }
}

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = CreatePurchaseViewModel(this, realm)
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.viewModel.favorites, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase_horizontal)
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.viewModel.frequents, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.viewModel.filteredSearchResults, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvSearchResults)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }
}

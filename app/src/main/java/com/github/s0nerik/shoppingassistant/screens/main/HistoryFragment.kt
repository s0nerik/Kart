package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.adapter_items.PurchaseHeader
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentHistoryBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import io.realm.Sort
import khronos.beginningOfDay
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class HistoryFragment : BaseBoundFragment<FragmentHistoryBinding>(R.layout.fragment_history) {
    private lateinit var realm: Realm

    private val historyItems = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LastAdapter.with(historyItems, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_recent_purchases)
                .map<PurchaseHeader>(R.layout.item_history_header)
                .into(recycler)

        loadHistoryItems()
    }

    private fun loadHistoryItems() {
        val purchases = realm.where(Purchase::class.java)
                .findAllSorted("date", Sort.DESCENDING)
                .groupBy { it.date!!.beginningOfDay }

        purchases.forEach {
            historyItems.add(PurchaseHeader(it.key))
            historyItems.addAll(it.value)
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
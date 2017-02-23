package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.View
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.adapter_items.HistoryHeader
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryHeaderBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class HistoryFragment : BaseBoundFragment<FragmentHistoryBinding>(R.layout.fragment_history) {
    private val historyItems = mutableListOf<Any>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LastAdapter.with(historyItems, BR.item)
                .type { Type<ItemHistoryBinding>(R.layout.item_history) }
                .type { Type<ItemHistoryHeaderBinding>(R.layout.item_history_header) }
                .into(recycler)

        loadHistoryItems()
    }

    private fun loadHistoryItems() {
        historyItems.clear()

        val purchases = realm.where(Purchase::class.java)
                .findAllSorted("date", Sort.DESCENDING)
                .groupBy { it.date!!.toDateTime().withTimeAtStartOfDay() }

        purchases.forEach {
            historyItems.add(HistoryHeader(it.key.toDate()))
            historyItems.addAll(it.value)
        }
    }
}
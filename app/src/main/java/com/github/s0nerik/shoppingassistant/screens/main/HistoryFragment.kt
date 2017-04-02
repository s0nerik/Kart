package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.adapter_items.HistoryHeader
import com.github.s0nerik.shoppingassistant.adapter_items.MoneySpent
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryHeaderBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class HistoryFragment : BaseBoundFragment<FragmentHistoryBinding>(R.layout.fragment_history) {
    private val historyItems = observableListOf<Any>()

    init {
        enterTransition = KTransitionSet.new {
            ordering(KTransitionSet.Ordering.SEQUENTIAL)
            transition(Fade(Fade.OUT)) { duration(0) }
            transitionSet {
                ordering(KTransitionSet.Ordering.TOGETHER)
                duration(200)
                interpolator(FastOutSlowInInterpolator())
                transition(Slide(Gravity.BOTTOM))
                transition(Fade(Fade.IN))
            }
        }

        exitTransition = KTransitionSet.new {
            duration(200)
            interpolator(FastOutSlowInInterpolator())
            transition(Fade(Fade.OUT))
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)

        LastAdapter.with(historyItems, BR.item)
                .type {
                    when (item) {
                        is Purchase -> Type<ItemHistoryBinding>(R.layout.item_history)
                        is HistoryHeader -> Type<ItemHistoryHeaderBinding>(R.layout.item_history_header)
                        else -> Type<ItemHistoryBinding>(R.layout.item_history)
                    }
                }
                .into(recycler)
    }

    override fun onResume() {
        super.onResume()
        initHistory()
    }

    private fun initHistory() {
        historyItems.clear()

        val purchases = realm.where(Purchase::class.java)
                .findAllSorted("date", Sort.DESCENDING)
                .groupBy { it.date!!.toDateTime().withTimeAtStartOfDay() }

        purchases.forEach {
            historyItems.add(HistoryHeader(it.key.toDate(), MoneySpent(it.value.sumByDouble { (it.amount * it.priceInDefaultCurrency).toDouble() }.toFloat(), MainPrefs.defaultCurrency)))
            historyItems.addAll(it.value)
        }
    }
}
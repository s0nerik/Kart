package com.github.s0nerik.shoppingassistant.screens.main.fragments.history

import android.support.v7.widget.RecyclerView
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.adapter_items.HistoryHeader
import com.github.s0nerik.shoppingassistant.adapter_items.MoneySpent
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryHeaderBinding
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.utils.weak

class HistoryViewModel : BaseViewModel() {
    private var interactor by weak<HistoryInteractor?>()

    private lateinit var mainRepo: IMainRepository

    val items = observableListOf<Any>()

    fun init(interactor: HistoryInteractor, mainRepo: IMainRepository = MainRepository) {
        this.interactor = interactor
        this.mainRepo = mainRepo

        mainRepo.getPurchases()
                .takeUntilCleared()
                .subscribe({
                    initHistory(it)
                }, {
                    // error
                })
    }

    private fun initHistory(purchases: List<Purchase>) {
        items.clear()

        purchases.groupBy { it.date.toDateTime().withTimeAtStartOfDay() }
                .forEach {
                    items.add(HistoryHeader(
                            it.key.toDate(),
                            MoneySpent(it.value.sumByDouble { (it.amount * it.priceInDefaultCurrency).toDouble() }.toFloat(), MainPrefs.defaultCurrency)
                    ))

                    items.addAll(it.value)
                }
    }

    fun initRecycler(recycler: RecyclerView) {
        recycler.setHasFixedSize(true)
        recycler.isNestedScrollingEnabled = false

        LastAdapter(items, BR.item)
                .type { item, _ ->
                    when (item) {
                        is Purchase -> Type<ItemHistoryBinding>(R.layout.item_history).onClick { interactor?.onItemSelected(it.binding.item!!) }
                        is HistoryHeader -> Type<ItemHistoryHeaderBinding>(R.layout.item_history_header)
                        else -> Type<ItemHistoryBinding>(R.layout.item_history)
                    }
                }
                .into(recycler)
    }
}
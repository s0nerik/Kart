package com.github.s0nerik.shoppingassistant.screens.main.purchase_lists

import android.support.v7.widget.RecyclerView
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseListsHeaderBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseListsItemBinding
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.screens.main.purchase_lists.items.Header
import com.github.s0nerik.shoppingassistant.screens.main.purchase_lists.items.Item
import com.github.s0nerik.shoppingassistant.utils.weak

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class PurchaseListsViewModel : BaseViewModel() {
    private lateinit var mainRepo: IMainRepository

    private var interactor by weak<PurchaseListsVmInteractor>()

    val items = observableListOf<Any>()

    fun init(interactor: PurchaseListsVmInteractor, mainRepo: IMainRepository = MainRepository) {
        this.interactor = interactor
        this.mainRepo = mainRepo

        mainRepo.getFuturePurchases()
                .map { it.groupBy { it.lastUpdate!!.toDateTime().withTimeAtStartOfDay() } }
                .subscribeUntilCleared {
                    items.clear()
                    it.forEach {
                        items.add(Header(it.value[0].lastUpdate!!))
                        items.addAll(it.value.map { Item(it) })
                    }
                }
    }

    fun initRecycler(recycler: RecyclerView) {
        with(recycler) {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)

            LastAdapter(items, BR.item)
                    .type { item, _ ->
                        when (item) {
                            is Item -> Type<ItemPurchaseListsItemBinding>(R.layout.item_purchase_lists_item)
                            is Header -> Type<ItemPurchaseListsHeaderBinding>(R.layout.item_purchase_lists_header)
                            else -> Type<ItemHistoryBinding>(R.layout.item_purchase_lists_item)
                        }
                    }
                    .into(recycler)
        }
    }

    fun addNewItem() {
        interactor?.let {
            it.provideNewItem()
                    .subscribeUntilCleared { items }
        }
    }
}
package com.github.s0nerik.shoppingassistant.screens.main

import android.databinding.ObservableList
import android.os.Bundle
import android.view.View
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentListsBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemListsHeaderBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemListsItemBinding
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.FuturePurchase
import com.github.s0nerik.shoppingassistant.screens.purchase.SelectItemActivity
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.save
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.dip
import java.util.*

/**
 * Created by Alex on 4/12/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class PurchasesListHeaderViewModel(val date: Date) {
    val readableDate: String
        get() = date.toDateTime().withTimeAtStartOfDay().toString("MMMM d")
}

class PurchasesListItemViewModel(
        val items: ObservableList<Any>,
        val futurePurchase: FuturePurchase
) {
    val item
        get() = futurePurchase.item

    fun confirm() {
        futurePurchase.confirm()
        items.remove(this)
    }

    // TODO: add 'undo' logic
    fun remove() {
        futurePurchase.remove()
        items.remove(this)
    }
}

class PurchaseListsViewModel {
    lateinit var f: PurchaseListsFragment

    fun addNewItem() {
        SelectItemActivity.startForResult(f.act)
                .bindUntilEvent(f, FragmentEvent.DESTROY)
                .subscribe { FuturePurchase(item = it, creationDate = Date(), lastUpdate = Date()).save() }
    }
}

class PurchaseListsFragment : BaseBoundFragment<FragmentListsBinding>(R.layout.fragment_lists) {
    private val listItems = observableListOf<Any>()
    private val vm = PurchaseListsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.f = this
        prepareList()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = vm

        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)

        LastAdapter.with(listItems, BR.item)
                .type {
                    when (item) {
                        is PurchasesListItemViewModel -> Type<ItemListsItemBinding>(R.layout.item_lists_item)
                                .onBind {
                                    val vm = item as PurchasesListItemViewModel

                                    binding.swipeLayout.close(false)
                                    binding.swipeLayout.removeSwipeListener(binding.swipeListener)
                                    binding.swipeListener = object : SimpleSwipeListener() {
                                        var startElevation: Float = 0f

                                        override fun onStartOpen(layout: SwipeLayout) {
                                            startElevation = binding.root.elevation
                                            binding.root.elevation = startElevation + dip(1)
                                        }

                                        override fun onOpen(layout: SwipeLayout) {
                                            when (layout.dragEdge) {
                                                SwipeLayout.DragEdge.Left -> vm.confirm()
                                                SwipeLayout.DragEdge.Right -> vm.remove()
                                            }
                                            // TODO: remove header if needed
                                            listItems.remove(item)
                                            binding.root.elevation = startElevation
                                        }

                                        override fun onClose(layout: SwipeLayout) {
                                            binding.root.elevation = startElevation
                                        }
                                    }
                                    binding.swipeLayout.addSwipeListener(binding.swipeListener)
                                }
                        is PurchasesListHeaderViewModel -> Type<ItemListsHeaderBinding>(R.layout.item_lists_header)
                        else -> Type<ItemHistoryBinding>(R.layout.item_lists_item)
                    }
                }
                .into(recycler)
    }

    private fun prepareList() {
        val results = realm.where(FuturePurchase::class.java)
                .findAllSortedAsync("lastUpdate", Sort.DESCENDING)

        val listener = RealmChangeListener<RealmResults<FuturePurchase>> { it ->
            listItems.clear()

            val futurePurchases = it.groupBy { it.lastUpdate!!.toDateTime().withTimeAtStartOfDay() }

            futurePurchases.forEach {
                listItems.add(PurchasesListHeaderViewModel(it.value[0].lastUpdate!!))
                listItems.addAll(it.value.map { PurchasesListItemViewModel(listItems, it) })
            }
        }

        results.addChangeListener(listener)

        lifecycle().filter { it == FragmentEvent.DESTROY }
                .firstElement()
                .subscribe { results.removeChangeListener(listener) }
    }
}
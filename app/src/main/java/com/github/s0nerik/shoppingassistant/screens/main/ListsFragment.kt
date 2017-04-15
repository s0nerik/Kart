package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.View
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.github.debop.kodatimes.toDateTime
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.adapter_items.ListHeader
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentListsBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemHistoryBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemListsHeaderBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemListsItemBinding
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast

/**
 * Created by Alex on 4/12/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class ListsFragment : BaseBoundFragment<FragmentListsBinding>(R.layout.fragment_lists) {
    private val listItems = observableListOf<Any>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)

        LastAdapter.with(listItems, BR.item)
                .type {
                    when (item) {
                        is Item -> Type<ItemListsItemBinding>(R.layout.item_lists_item)
                                .onBind {
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
                                                SwipeLayout.DragEdge.Left -> toast("Confirm")
                                                SwipeLayout.DragEdge.Right -> toast("Remove")
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
                        is ListHeader -> Type<ItemListsHeaderBinding>(R.layout.item_lists_header)
                        else -> Type<ItemHistoryBinding>(R.layout.item_lists_item)
                    }
                }
                .into(recycler)
    }

    override fun onResume() {
        super.onResume()
        initLists()
    }

    // TODO: create class for Purchase List item
    private fun initLists() {
        listItems.clear()

        val purchases = realm.where(Purchase::class.java)
                .findAllSorted("date", Sort.DESCENDING)
                .groupBy { it.date!!.toDateTime().withTimeAtStartOfDay() }

        purchases.forEach {
            listItems.add(ListHeader(it.key.toDate()))
            listItems.addAll(it.value.map { it.item!! })
        }
    }
}
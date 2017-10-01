package com.github.s0nerik.shoppingassistant.screens.main.fragments

import android.content.Context
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.AutoTransition
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.anim.Scale
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentCartBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemCartBinding
import com.github.s0nerik.shoppingassistant.ext.KTransition
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.model.RealmCart
import com.github.s0nerik.shoppingassistant.model.RealmPurchase
import com.github.s0nerik.shoppingassistant.screens.purchase.SelectItemActivity
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_cart.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartViewModel(val f: CartFragment) {
    fun createNewPurchase() {
        SelectItemActivity.startForResult(f.act)
                .bindUntilEvent(f, FragmentEvent.DESTROY)
                .subscribe { RealmCart.add(it) }
    }

    fun saveCart() {
        RealmCart.save()
    }

    fun clearCart() {
        RealmCart.clear()
    }
}

class CartFragment : BaseBoundFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    private val cartChangedListener = object : ObservableList.OnListChangedCallback<ObservableList<RealmPurchase>>() {
        private val changeBottomButtonsVisibility = { list: ObservableList<RealmPurchase> ->
            if (list.size == 0) {
                if (bottomButtons.visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(root, KTransitionSet.new {
                        transitionSet {
                            view(bottomButtons)
                            transition(Slide())
                            transition(Fade())
                        }
                        transition(AutoTransition()) { exclude(bottomButtons) }
                    })
                    bottomButtons.visibility = View.GONE
                    emptyCart.visibility = View.VISIBLE
                }
            } else {
                if (bottomButtons.visibility != View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(root, KTransitionSet.new {
                        transitionSet {
                            view(bottomButtons)
                            transition(Slide())
                            transition(Fade())
                        }
                        transition(AutoTransition()) { exclude(bottomButtons) }
                    })
                    bottomButtons.visibility = View.VISIBLE
                    emptyCart.visibility = View.GONE
                }
            }
        }

        override fun onItemRangeMoved(p0: ObservableList<RealmPurchase>?, p1: Int, p2: Int, p3: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onChanged(p0: ObservableList<RealmPurchase>?) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeChanged(p0: ObservableList<RealmPurchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeInserted(p0: ObservableList<RealmPurchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeRemoved(p0: ObservableList<RealmPurchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
    }

    init {
        exitTransition = KTransition.new(Fade(Fade.OUT)) { duration(200) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        enterTransition = KTransitionSet.new {
            ordering(KTransitionSet.Ordering.SEQUENTIAL)
            transition(Fade(Fade.OUT)) { duration(0); views(R.id.fab, R.id.recycler, R.id.emptyCart, R.id.bottomButtons) }
            transitionSet {
                ordering(KTransitionSet.Ordering.TOGETHER)
                transitionSet {
                    view(R.id.recycler)
                    duration(200)
                    interpolator(FastOutSlowInInterpolator())
                    transition(Slide(Gravity.BOTTOM))
                    transition(Fade(Fade.IN))
                }
                if (RealmCart.isEmpty()) {
                    transition(Fade(Fade.IN)) { view(R.id.emptyCart); duration(200) }
                } else {
                    transitionSet {
                        view(R.id.bottomButtons)
                        duration(200)
                        delay(100)
                        interpolator(FastOutSlowInInterpolator())
                        transition(Slide(Gravity.BOTTOM))
                        transition(Fade(Fade.IN))
                    }
                }
                transitionSet {
                    view(R.id.fab)
                    duration(200)
                    delay(if (RealmCart.isEmpty()) 100 else 300)
                    interpolator(FastOutSlowInInterpolator())
                    transition(Scale(0.7f))
                    transition(Fade(Fade.IN))
                    transition(Slide(Gravity.BOTTOM))
                }
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = CartViewModel(this)

        if (RealmCart.isEmpty()) {
            bottomButtons.visibility = View.GONE
            emptyCart.visibility = View.VISIBLE
        } else {
            emptyCart.visibility = View.GONE
        }

        initCart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RealmCart.purchases.addOnListChangedCallback(cartChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        RealmCart.purchases.removeOnListChangedCallback(cartChangedListener)
    }

    fun initCart() {
        LastAdapter(RealmCart.purchases, BR.item)
                .type { _, _ -> Type<ItemCartBinding>(R.layout.item_cart) }
                .into(recycler)

        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)
    }
}
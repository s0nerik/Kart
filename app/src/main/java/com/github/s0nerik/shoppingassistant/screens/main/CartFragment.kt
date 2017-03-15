package com.github.s0nerik.shoppingassistant.screens.main

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
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransition
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import kotlinx.android.synthetic.main.fragment_cart.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartViewModel(val f: CartFragment) {
    fun createNewPurchase() {
        f.startActivity<CreatePurchaseActivity>()
    }

    fun saveCart() {
        Cart.save()
    }

    fun clearCart() {
        Cart.clear()
    }
}

class CartFragment : BaseBoundFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    private val cartChangedListener = object : ObservableList.OnListChangedCallback<ObservableList<Purchase>>() {
        private val changeBottomButtonsVisibility = { list: ObservableList<Purchase> ->
            if (list.size == 0) {
                if (bottomButtons.visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(root, KTransitionSet.new {
                        transitionSet {
                            view(bottomButtons)
                            transition(Slide())
                            transition(Fade())
                        }
                        transition(AutoTransition().excludeTarget(bottomButtons, true))
                    })
                    bottomButtons.visibility = View.GONE
                }
            } else {
                if (bottomButtons.visibility != View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(root, KTransitionSet.new {
                        transitionSet {
                            view(bottomButtons)
                            transition(Slide())
                            transition(Fade())
                        }
                        transition(AutoTransition().excludeTarget(bottomButtons, true))
                    })
                    bottomButtons.visibility = View.VISIBLE
                }
            }
        }

        override fun onItemRangeMoved(p0: ObservableList<Purchase>?, p1: Int, p2: Int, p3: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onChanged(p0: ObservableList<Purchase>?) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeChanged(p0: ObservableList<Purchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeInserted(p0: ObservableList<Purchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
        override fun onItemRangeRemoved(p0: ObservableList<Purchase>?, p1: Int, p2: Int) = changeBottomButtonsVisibility(p0!!)
    }

    init {
        exitTransition = KTransition.new(Fade(Fade.OUT)) { duration(200) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        enterTransition = KTransitionSet.new {
            ordering(KTransitionSet.Ordering.SEQUENTIAL)
            transition(Fade(Fade.OUT)) { duration(0); views(R.id.fab, R.id.recycler, R.id.bottomButtons) }
            transitionSet {
                ordering(KTransitionSet.Ordering.TOGETHER)
                transitionSet {
                    view(R.id.recycler)
                    duration(200)
                    interpolator(FastOutSlowInInterpolator())
                    transition(Slide(Gravity.BOTTOM))
                    transition(Fade(Fade.IN))
                }
                if (!Cart.isEmpty()) {
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
                    delay(if (Cart.isEmpty()) 100 else 300)
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

        if (Cart.isEmpty())
            bottomButtons.visibility = View.GONE

        initCart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Cart.purchases.addOnListChangedCallback(cartChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Cart.purchases.removeOnListChangedCallback(cartChangedListener)
    }

    fun initCart() {
        LastAdapter.with(Cart.purchases, BR.item)
                .type { Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recycler)

        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)
    }
}
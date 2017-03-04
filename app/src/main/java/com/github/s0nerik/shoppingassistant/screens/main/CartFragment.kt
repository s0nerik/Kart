package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.anim.Scale
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.currentCart
import com.github.s0nerik.shoppingassistant.databinding.FragmentCartBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransition
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import kotlinx.android.synthetic.main.fragment_cart.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartViewModel(val f: CartFragment) {
    val cart = currentCart

    fun createNewPurchase() {
        f.startActivity<CreatePurchaseActivity>()
    }
}

class CartFragment : BaseBoundFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    val vm: CartViewModel by lazy { binding.vm }

    init {
        enterTransition = KTransitionSet.new {
            ordering(KTransitionSet.Ordering.SEQUENTIAL)
            transition(Fade(Fade.OUT)) { duration(0); view(R.id.fab) }
            transitionSet {
                ordering(KTransitionSet.Ordering.TOGETHER)
                transitionSet {
                    view(R.id.recycler)
                    duration(200)
                    interpolator(FastOutSlowInInterpolator())
                    transition(Slide(Gravity.BOTTOM))
                    transition(Fade(Fade.IN))
                }
                transitionSet {
                    view(R.id.fab)
                    duration(200)
                    delay(100)
                    interpolator(FastOutSlowInInterpolator())
                    transition(Scale(0.7f))
                    transition(Fade(Fade.IN))
                }
            }
        }

        exitTransition = KTransition.new(Fade(Fade.OUT)) { duration(200) }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = CartViewModel(this)

        initCart()
    }

    private fun initCart() {
        LastAdapter.with(vm.cart.purchasesObservableList, BR.item)
                .type { Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recycler)

        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)
    }
}
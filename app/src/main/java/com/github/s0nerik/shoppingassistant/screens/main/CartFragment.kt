package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.currentCart
import com.github.s0nerik.shoppingassistant.databinding.FragmentCartBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import kotlinx.android.synthetic.main.fragment_cart.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartViewModel(val f: CartFragment) {
    val cart = currentCart
}

class CartFragment : BaseBoundFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    val vm: CartViewModel by lazy { binding.vm }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = CartViewModel(this)

        initCart()
    }

    private fun initCart() {
        LastAdapter.with(vm.cart.purchases, BR.item)
                .type { Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recycler)

        recycler.isNestedScrollingEnabled = false
        recycler.setHasFixedSize(true)
    }
}
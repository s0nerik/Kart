package com.github.s0nerik.shoppingassistant.screens.main.cart

import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemCartBinding
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.utils.weak

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartViewModel : BaseViewModel() {
    private var interactor by weak<CartVmInteractor>()

    val items = Cart.get()

    fun init(interactor: CartVmInteractor) {
        this.interactor = interactor
    }

    fun initRecycler(recycler: RecyclerView) {
        with(recycler) {
            LastAdapter(Cart.get(), BR.item)
                    .type { _, _ -> Type<ItemCartBinding>(R.layout.item_cart) }
                    .into(recycler)

            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }
    }

    fun createNewPurchase() {
        interactor?.let {
            it.createNewItem()
                    .subscribeUntilCleared { Cart.add(it) }
        }
    }

    fun saveCart() {
        Cart.save()
    }

    fun clearCart() {
        Cart.clear()
    }

    fun isEmpty(list: List<Any>) {
        list.isEmpty()
    }
}
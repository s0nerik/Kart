package com.github.s0nerik.shoppingassistant.screens.main.cart

import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentCartBinding
import com.github.s0nerik.shoppingassistant.screens.select_item.SelectItemActivity
import kotlinx.android.synthetic.main.fragment_cart.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class CartFragment : BaseBoundVmFragment<FragmentCartBinding, CartViewModel>(
    R.layout.fragment_cart, CartViewModel::class
), CartVmInteractor {
    init {
        CartAnimator.attach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(this)
        vm.initRecycler(recycler)
    }

    override fun createNewItem() = SelectItemActivity.startForResult(act)
}
package com.github.s0nerik.shoppingassistant.screens.main.purchase_lists

import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentPurchaseListsBinding
import com.github.s0nerik.shoppingassistant.screens.select_item.SelectItemActivity
import kotlinx.android.synthetic.main.fragment_purchase_lists.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class PurchaseListsFragment : BaseBoundVmFragment<FragmentPurchaseListsBinding, PurchaseListsViewModel>(
    R.layout.fragment_purchase_lists, PurchaseListsViewModel::class
), PurchaseListsVmInteractor {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(this)
        vm.initRecycler(recycler)
    }

    override fun provideNewItem() = SelectItemActivity.startForResult(act)
}
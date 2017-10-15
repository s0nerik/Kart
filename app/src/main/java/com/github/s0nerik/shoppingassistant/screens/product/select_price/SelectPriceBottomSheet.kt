package com.github.s0nerik.shoppingassistant.screens.product.select_price

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.SheetSelectPriceBinding
import kotlinx.android.synthetic.main.sheet_select_price.*


/**
 * Created by Alex Isaienko on 10/15/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectPriceBottomSheet : BaseBottomSheet<SheetSelectPriceBinding, SelectPriceViewModel>(
        R.layout.sheet_select_price, SelectPriceViewModel::class
) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.initQuantityQualifierSpinner(spinnerQuantityQualifier)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        vm.cancel()
    }
}
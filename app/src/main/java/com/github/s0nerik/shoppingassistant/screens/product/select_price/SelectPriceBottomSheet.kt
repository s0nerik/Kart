package com.github.s0nerik.shoppingassistant.screens.product.select_price

import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.R.id.spinnerQuantityQualifier
import com.github.s0nerik.shoppingassistant.databinding.SheetSelectPriceBinding
import com.github.s0nerik.shoppingassistant.model.Price
import io.reactivex.Maybe
import java.util.*


/**
 * Created by Alex Isaienko on 10/15/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
//class SelectPriceBottomSheet : BaseBottomSheet<SheetSelectPriceBinding, SelectPriceViewModel, Price>(
//        R.layout.sheet_select_price, SelectPriceViewModel::class
//), SelectPriceViewModelInteractor {
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        vm.init(this)
//        vm.initQuantityQualifierSpinner(spinnerQuantityQualifier)
//    }
//
//    override fun selectCurrency(current: Currency): Maybe<Currency> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
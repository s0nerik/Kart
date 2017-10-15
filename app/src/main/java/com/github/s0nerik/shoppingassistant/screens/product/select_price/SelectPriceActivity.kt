package com.github.s0nerik.shoppingassistant.screens.product.select_price

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectShopBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Price
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_select_price.*
import java.util.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectPriceActivity : BaseBoundVmActivity<ActivitySelectShopBinding, SelectPriceViewModel>(
        R.layout.activity_select_price, SelectPriceViewModel::class
), SelectPriceViewModelInteractor {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.init(this)
        vm.initQuantityQualifierSpinner(spinnerQuantityQualifier)
    }

    override fun selectCurrency(current: Currency): Maybe<Currency> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finishWithResult(price: Price?) {
        if (price != null)
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_PRICE, price))
        finish()
    }

    companion object {
        private const val EXTRA_PRICE = "EXTRA_PRICE"
        fun startForResult(a: Activity): Maybe<Price> {
            return a.startForResult<SelectPriceActivity, Price>(EXTRA_PRICE)
        }
    }
}
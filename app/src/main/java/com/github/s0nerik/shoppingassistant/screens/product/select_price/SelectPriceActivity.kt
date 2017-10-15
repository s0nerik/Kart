package com.github.s0nerik.shoppingassistant.screens.product.select_price

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectShopBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Price
import io.reactivex.Maybe
import java.util.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectPriceActivity : BaseBoundActivity<ActivitySelectShopBinding>(
        R.layout.activity_select_price
), SelectPriceViewModelInteractor {
    private val fragment = SelectPriceBottomSheet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragment.show(supportFragmentManager, null)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SelectPriceBottomSheet)
            fragment.vm.init(this@SelectPriceActivity)
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
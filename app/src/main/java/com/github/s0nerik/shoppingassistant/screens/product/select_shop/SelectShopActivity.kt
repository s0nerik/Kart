package com.github.s0nerik.shoppingassistant.screens.product.select_shop

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectShopBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Shop
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_select_shop.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectShopActivity : BaseBoundVmActivity<ActivitySelectShopBinding, SelectShopViewModel>(
        R.layout.activity_select_shop, SelectShopViewModel::class
), SelectShopViewModelInteractor {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.interactor = this
        vm.initRecycler(recycler)
        vm.shops.observe(this, Observer {  })
    }

    override fun finishWithResult(shop: Shop?) {
        if (shop != null)
            setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    companion object {
        private const val EXTRA_SHOP = "EXTRA_SHOP"
        fun startForResult(a: Activity): Maybe<Shop> {
            return a.startForResult<SelectShopActivity, Shop>(EXTRA_SHOP)
        }
    }
}
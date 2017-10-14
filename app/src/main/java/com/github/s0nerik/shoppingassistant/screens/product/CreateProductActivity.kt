package com.github.s0nerik.shoppingassistant.screens.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreateProductBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Price
import com.github.s0nerik.shoppingassistant.model.Shop
import com.github.s0nerik.shoppingassistant.screens.product.select_category.SelectCategoryActivity
import com.github.s0nerik.shoppingassistant.screens.product.select_shop.SelectShopActivity
import io.reactivex.Maybe
import org.jetbrains.anko.bundleOf

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class CreateProductActivity : BaseBoundVmActivity<ActivityCreateProductBinding, CreateProductViewModel>(
        R.layout.activity_create_product, CreateProductViewModel::class
), CreateProductViewModelInteractor {
    companion object {
        private val EXTRA_NAME = "EXTRA_NAME"
        private val EXTRA_ITEM = "EXTRA_ITEM"

        fun startForResult(a: Activity, searchQuery: String = ""): Maybe<Item> {
            return a.startForResult<CreateProductActivity, Item>(EXTRA_ITEM, bundleOf(EXTRA_NAME to searchQuery))
        }
    }

    private val animator = CreateProductAnimator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.interactor = this

        val extraName = intent.getStringExtra(EXTRA_NAME)
        extraName?.let { vm.name = it.capitalize() }

        animator.appear()
    }

    override fun selectPrice(): Maybe<Price> {
//        SelectShopBottomSheet(this)
        TODO("not implemented")
    }

    override fun selectCategory(): Maybe<Category> = SelectCategoryActivity.startForResult(this)
    override fun selectShop(): Maybe<Shop> = SelectShopActivity.startForResult(this)

    override fun finishWithResult(item: Item?) {
        if (item != null)
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ITEM, item))
        finish()
    }

    override fun finish() {
        animator.disappear { super.finish() }
    }
}
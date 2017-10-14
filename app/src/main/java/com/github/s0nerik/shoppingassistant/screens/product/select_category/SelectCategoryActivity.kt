package com.github.s0nerik.shoppingassistant.screens.product.select_category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectCategoryBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Category
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_select_category.*


/**
 * Created by Alex Isaienko on 10/14/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectCategoryActivity : BaseBoundVmActivity<ActivitySelectCategoryBinding, SelectCategoryViewModel>(
        R.layout.activity_select_category,
        SelectCategoryViewModel::class
), SelectCategoryViewModelInteractor {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.init(this, this)
        vm.initRecycler(recycler)
    }

    override fun finishWithResult(category: Category?) {
        if (category != null)
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_CATEGORY, category))
        finish()
    }

    companion object {
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        fun startForResult(a: Activity): Maybe<Category> {
            return a.startForResult<SelectCategoryActivity, Category>(EXTRA_CATEGORY)
        }
    }
}
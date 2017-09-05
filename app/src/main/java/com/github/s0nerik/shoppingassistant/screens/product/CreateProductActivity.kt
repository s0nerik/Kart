package com.github.s0nerik.shoppingassistant.screens.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreateProductBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Maybe
import rx_activity_result2.RxActivityResult

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class CreateProductActivity : BaseBoundActivity<ActivityCreateProductBinding>(R.layout.activity_create_product) {
    companion object {
        const val EXTRA_ID = "id"

        private fun intent(ctx: Context, productName: String? = null): Intent {
            val intent = Intent(ctx, CreateProductActivity::class.java)
            intent.putExtra("name", productName)
            return intent
        }

        fun startForResult(a: Activity, searchQuery: String = ""): Maybe<Purchase> {
            return RxActivityResult.on(a)
                    .startIntent(intent(a, searchQuery))
                    .firstElement()
                    .filter { it.resultCode() == Activity.RESULT_OK }
                    .map { result -> Purchase().queryFirst { it.equalTo("id", result.data().getStringExtra(EXTRA_ID)) }!! }
        }
    }

    private val animator = CreateProductAnimator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreateProductViewModel(this, realm)

        val extraName = intent.getStringExtra("name")
        extraName?.let { binding.vm!!.setName(it.capitalize()) }

        animator.appear()
    }

    override fun finish() {
        animator.disappear { super.finish() }
    }
}
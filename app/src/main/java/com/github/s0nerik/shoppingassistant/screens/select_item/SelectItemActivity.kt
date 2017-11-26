package com.github.s0nerik.shoppingassistant.screens.select_item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.WindowManager
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectItemBinding
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.screens.product.CreateProductActivity
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_select_item.*

class SelectItemActivity : BaseBoundVmActivity<ActivitySelectItemBinding, SelectItemViewModel>(
    R.layout.activity_select_item, SelectItemViewModel::class
), SelectItemVmInteractor {
    private var selectedItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        vm.init(this)
        vm.initViews(rvFavorites, rvFrequents, rvSearchResults)
    }

    override fun provideVoiceRecognitionResult(): Maybe<String> {
        return startForResult(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), VOICE_SEARCH_REQ_CODE)
                .map {
                    it.data?.let { it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0] } ?: ""
                }
    }

    override fun createItem(name: String): Maybe<Item> {
        return CreateProductActivity.startForResult(this, name)
    }

    override fun onItemSelected(item: Item) {
        selectedItem = item
        finish()
    }

    override fun finish() {
        selectedItem?.let {
            setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_ITEM, it))
        } ?: setResult(Activity.RESULT_CANCELED)
        super.finish()
    }

    companion object {
        private const val SELECTED_ITEM = "SELECTED_ITEM"
        private const val VOICE_SEARCH_REQ_CODE = 672

        fun startForResult(a: Activity): Maybe<Item> =
                a.startForResult<SelectItemActivity, Item>(SELECTED_ITEM)
    }
}
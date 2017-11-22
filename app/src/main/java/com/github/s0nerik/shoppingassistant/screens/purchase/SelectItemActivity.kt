package com.github.s0nerik.shoppingassistant.screens.purchase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.WindowManager
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemHorizontalBinding
import com.github.s0nerik.shoppingassistant.ext.RecyclerDivider
import com.github.s0nerik.shoppingassistant.ext.startForResult
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.RealmItem
import com.github.s0nerik.shoppingassistant.screens.product.CreateProductActivity
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_select_item.*
import rx_activity_result2.RxActivityResult

class SelectItemActivity : BaseBoundVmActivity<ActivitySelectItemBinding, SelectItemViewModel>(
    R.layout.activity_select_item, SelectItemViewModel::class
), SelectItemVmInteractor {
    private lateinit var animator: SelectItemActivityAnimator
    private var selectedItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        vm.init(this)
        vm.initViews(rvFavorites, rvFrequents, rvSearchResults)

        animator = SelectItemActivityAnimator(this, binding)
        animator.appear()
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
        animator.disappear {
            selectedItem?.let {
                setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_ITEM, it.id))
            } ?: setResult(Activity.RESULT_CANCELED)
            super.finish()
        }
    }

    companion object {
        private const val SELECTED_ITEM = "SELECTED_ITEM"
        private const val VOICE_SEARCH_REQ_CODE = 672

        fun startForResult(a: Activity): Maybe<Item> =
                a.startForResult<SelectItemActivity, Item>(SELECTED_ITEM)
    }
}

//class SelectItemActivity : BaseBoundActivity<ActivitySelectItemBinding>(R.layout.activity_select_item) {
//
//    private val itemAdapterType = Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item)
//            .onClick { finishWithResult(it.binding.item!!) }
//
//    private val horizontalItemAdapterType = Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal)
//            .onClick { finishWithResult(it.binding.item!!) }
//
//    private lateinit var animator: SelectItemActivityAnimator
//    private var selectedItem: Item? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        binding.vm = SelectItemViewModel(this)
//        initData()
//        animator = SelectItemActivityAnimator(this, binding)
//        animator.appear()
//    }
//
//    override fun finish() {
//        animator.disappear {
//            if (selectedItem != null)
//                setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_ITEM_ID, selectedItem!!.id))
//            else
//                setResult(Activity.RESULT_CANCELED)
//            super.finish()
//        }
//    }
//
//    private fun finishWithResult(item: Item) {
//        selectedItem = item
//        finish()
//    }
//
//    private fun initData() {
//        initFavorites()
//        initFrequents()
//        initSearchResults()
//    }
//
//    private fun initFavorites() {
//        LastAdapter(binding.vm!!.favorites, BR.item)
//                .type { _, _ -> horizontalItemAdapterType }
//                .into(rvFavorites)
//
//        rvFavorites.isNestedScrollingEnabled = false
//        rvFavorites.setHasFixedSize(true)
//
//        rvFavorites.addItemDecoration(RecyclerDivider.vertical)
//    }
//
//    private fun initFrequents() {
//        LastAdapter(binding.vm!!.frequents, BR.item)
//                .type { _, _ -> itemAdapterType }
//                .into(rvFrequents)
//
//        rvFrequents.isNestedScrollingEnabled = false
//        rvFrequents.addItemDecoration(RecyclerDivider.horizontal)
//    }
//
//    private fun initSearchResults() {
//        LastAdapter(binding.vm!!.filteredSearchResults, BR.item)
//                .type { _, _ -> itemAdapterType }
//                .into(rvSearchResults)
//
//        rvSearchResults.isNestedScrollingEnabled = false
//    }
//
//    fun createProduct() {
//        CreateProductActivity.startForResult(this, etSearch.text.toString())
//                .bindUntilEvent(this, ActivityEvent.DESTROY)
//                .subscribe {
//                    with(it) {
//                        binding.vm!!.frequents.add(it)
//                        binding.vm!!.items.add(it)
//                        if (it.isFavorite)
//                            binding.vm!!.favorites.add(it)
//                    }
//                    Cart.add(it)
//                }
//    }
//
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == binding.vm!!.VOICE_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK) {
////            data?.apply {
////                binding.vm!!.notifyVoiceSearchResult(getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0])
////            }
////        }
////    }
//
//    companion object {
//        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"
//
//        fun startForResult(a: Activity): Maybe<Item> {
//            return RxActivityResult.on(a)
//                    .startIntent(Intent(a, SelectItemActivity::class.java))
//                    .firstElement()
//                    .filter { it.resultCode() == Activity.RESULT_OK }
//                    .map {
//                        val itemId = it.data().getStringExtra(SelectItemActivity.SELECTED_ITEM_ID)
//                        Item.from(RealmItem().queryFirst { it.equalTo("id", itemId) }!!)
//                    }
//        }
//    }
//}

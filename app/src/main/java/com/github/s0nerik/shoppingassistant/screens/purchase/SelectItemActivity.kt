package com.github.s0nerik.shoppingassistant.screens.purchase

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemHorizontalBinding
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.screens.product.CreateProductActivity
import com.github.s0nerik.shoppingassistant.screens.product.EXTRA_ID
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.queryFirst
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_select_item.*
import org.jetbrains.anko.dip
import rx_activity_result.RxActivityResult
import java.util.*

class SelectItemViewModel(
        private val activity: SelectItemActivity,
        private val realm: Realm
) : BaseObservable() {
    val VOICE_SEARCH_REQ_CODE = 672

    val isSearching = ObservableBoolean(false)

    val frequents by lazy { observableListOf(frequentItems(realm)) }
    val favorites by lazy { observableListOf(favoriteItems(realm)) }
    val items by lazy { observableListOf(items(realm)) }

    val filteredSearchResults = ObservableArrayList<Item>()

    init {
        activity.apply {
            etSearch.textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe { s ->
                        isSearching.set(s.isNotEmpty())

                        filteredSearchResults.clear()
                        filteredSearchResults += items.filter { it.readableName.contains(s, true) }
                    }
        }
    }

    fun clearSearch() {
        activity.etSearch.setText("")
    }

    fun voiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        activity.startActivityForResult(intent, VOICE_SEARCH_REQ_CODE)
    }

    fun notifyVoiceSearchResult(text: String) {
        activity.etSearch.setText(text)
    }

    fun createProduct() {
        activity.createProduct()
    }
}

class SelectItemActivityAnimator(val a: SelectItemActivity, val binding: ActivitySelectItemBinding) {
    fun appear(callback: (() -> Unit)? = null) {
        animate(true, callback)
    }

    fun disappear(callback: (() -> Unit)? = null) {
        animate(false, callback)
    }

    private fun animate(appear: Boolean, callback: (() -> Unit)?) {
        with(a) {
            var views = listOf(bg, searchCard, btnCreateNewProduct, favoritesCard, frequentsCard)

            if (appear) {
                scrollView.applyWrongNestedScrollWorkaround()
                views.subList(1, views.size).forEach {
                    it.translationY = dip(80).toFloat()
                    it.alpha = 0f
                }
            } else {
                views = views.reversed()
            }

            val durations = arrayOf(500, 200, 200, 200, 200)
            val delays = if (binding.vm!!.favorites.isNotEmpty()) {
                arrayOf(0, 0, 200, 400, 600)
            } else {
                arrayOf(0, 0, 200, 400, 400)
            }

            val interpolator = if (appear) FastOutSlowInInterpolator() else DecelerateInterpolator(2f)

            val anim = AnimatorSet()
            anim.playTogether(
                    views.mapIndexed { i, view ->
                        ViewPropertyObjectAnimator.animate(view)
                                .alpha(if (appear) 1f else 0f)
                                .translationY(if (appear) 0f else dip(80).toFloat())
                                .setDuration(durations[i].toLong())
                                .setStartDelay(delays[i].toLong())
                                .setInterpolator(interpolator)
                                .get()
                    }
            )
            callback?.let {
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator?) = it()
                    override fun onAnimationEnd(animation: Animator?) = it()
                })
            }
            anim.start()
        }
    }
}

class SelectItemActivity : BaseBoundActivity<ActivitySelectItemBinding>(R.layout.activity_select_item) {

    private val itemAdapterType = Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item)
            .onClick { finishWithResult(binding.item) }

    private val horizontalItemAdapterType = Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal)
            .onClick { finishWithResult(binding.item) }

    private lateinit var animator: SelectItemActivityAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding.vm = SelectItemViewModel(this, realm)
        initData()
        animator = SelectItemActivityAnimator(this, binding)
        animator.appear()
    }

    override fun finish() {
        animator.disappear { super.finish() }
    }

    private fun finishWithResult(item: Item) {
        setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_ITEM_ID, item.id))
        finish()
    }

    private fun initData() {
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.vm!!.favorites, BR.item)
                .type { horizontalItemAdapterType }
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.vm!!.frequents, BR.item)
                .type { itemAdapterType }
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.vm!!.filteredSearchResults, BR.item)
                .type { itemAdapterType }
                .into(rvSearchResults)

        rvSearchResults.isNestedScrollingEnabled = false
    }

    fun createProduct() {
        RxActivityResult.on(this)
                .startIntent(CreateProductActivity.intent(this, etSearch.text.toString()))
                .filter { it.resultCode() == Activity.RESULT_OK }
                .subscribe { result ->
                    with (Purchase().queryFirst { it.equalTo("id", result.data().getStringExtra(EXTRA_ID)) }!!) {
                        binding.vm!!.frequents.add(item!!)
                        binding.vm!!.items.add(item!!)
                        if (item!!.isFavorite)
                            binding.vm!!.favorites.add(item)

                        Cart.add(this)
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == binding.vm!!.VOICE_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK) {
            data?.apply {
                binding.vm!!.notifyVoiceSearchResult(getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0])
            }
        }
    }

    companion object {
        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"

        fun startForResult(f: BaseBoundFragment<*>, listener: (Item) -> Unit = {
            Cart.add(Purchase(item = it, date = Date()))
        }) {
            RxActivityResult.on(f)
                    .startIntent(Intent(f.activity, SelectItemActivity::class.java))
                    .bindUntilEvent(f, FragmentEvent.DESTROY)
                    .subscribe {
                        val itemId = it.data().getStringExtra(SelectItemActivity.SELECTED_ITEM_ID)
                        val selectedItem = Item().queryFirst { it.equalTo("id", itemId) }
                        listener(selectedItem!!)
                    }
        }
    }
}

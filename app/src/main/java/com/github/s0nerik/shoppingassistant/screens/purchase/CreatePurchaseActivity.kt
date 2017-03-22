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
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
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
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.queryFirst
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_purchase.*
import org.jetbrains.anko.dip
import rx_activity_result.RxActivityResult
import java.util.*

class CreatePurchaseViewModel(
        private val activity: CreatePurchaseActivity,
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

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {

    private val itemAdapterType = Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item)
            .onClick {
                Cart.add(Purchase(item = binding.item, date = Date()))
                finish()
            }
    private val horizontalItemAdapterType = Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal)
            .onClick {
                Cart.add(Purchase(item = binding.item, date = Date()))
                finish()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding.vm = CreatePurchaseViewModel(this, realm)
        initData()
        animate(true)
    }

    private fun animate(appear: Boolean) {
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
        val delays = if (binding.vm.favorites.isNotEmpty()) {
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
        if (!appear) {
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) = super@CreatePurchaseActivity.finish()
                override fun onAnimationEnd(animation: Animator?) = super@CreatePurchaseActivity.finish()
            })
        }
        anim.start()
    }

    override fun finish() {
        animate(false)
//        TransitionManager.beginDelayedTransition(root, KTransitionSet.new {
//            transitionSet {
//                views(frequentsCard)
//                duration(200)
//                interpolator(FastOutSlowInInterpolator())
//                transition(Fade(Fade.OUT))
//                transition(Slide(Gravity.BOTTOM))
//            }
//
//            transitionSet {
//                views(favoritesCard)
//                duration(200)
//                delay(100)
//                interpolator(FastOutSlowInInterpolator())
//                transition(Fade(Fade.OUT))
//                transition(Slide(Gravity.BOTTOM))
//            }
//
//            transitionSet {
//                view(btnCreateNewProduct)
//                duration(200)
//                delay(200)
//                interpolator(FastOutSlowInInterpolator())
//                transition(Fade(Fade.OUT))
//                transition(Slide(Gravity.BOTTOM))
//            }
//
//            transitionSet {
//                view(searchCard)
//                duration(200)
//                delay(300)
//                interpolator(FastOutSlowInInterpolator())
//                transition(Fade(Fade.OUT))
//                transition(Slide(Gravity.BOTTOM))
//            }
//
//            transition(Fade(Fade.OUT)) {
//                view(bg)
//                duration(200)
//                delay(300)
//                interpolator(FastOutSlowInInterpolator())
//            }
//
//            onEnd { super.finish() }
//            onCancel { super.finish() }
//        })
//
//        frequentsCard.visibility = View.INVISIBLE
//        favoritesCard.visibility = View.INVISIBLE
//        searchCard.visibility = View.INVISIBLE
//        btnCreateNewProduct.visibility = View.INVISIBLE
//        bg.visibility = View.INVISIBLE
    }

    private fun initData() {
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.vm.favorites, BR.item)
                .type { horizontalItemAdapterType }
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.vm.frequents, BR.item)
                .type { itemAdapterType }
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.vm.filteredSearchResults, BR.item)
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
                        binding.vm.frequents.add(item!!)
                        binding.vm.items.add(item!!)
                        if (item!!.isFavorite)
                            binding.vm.favorites.add(item)

                        Cart.add(this)
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == binding.vm.VOICE_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK) {
            data?.apply {
                binding.vm.notifyVoiceSearchResult(getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0])
            }
        }
    }
}

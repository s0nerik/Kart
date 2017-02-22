package com.github.s0nerik.shoppingassistant.screens.purchase

import android.app.Activity
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.speech.RecognizerIntent
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemHorizontalBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.screens.product.CreateProductActivity
import com.github.s0nerik.shoppingassistant.screens.product.EXTRA_ID
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.queryFirst
import io.realm.ItemRealmProxy
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_purchase.*
import rx_activity_result.RxActivityResult

class CreatePurchaseViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm
) : BaseObservable() {
    val VOICE_SEARCH_REQ_CODE = 672

    val isSearching = ObservableBoolean(false)

    val frequents by lazy { observableListOf(frequentItems(realm)) }
    val favorites by lazy { observableListOf(favoriteItems(realm)) }
    private val purchases by lazy { observableListOf(purchases(realm)) }

    val filteredSearchResults = ObservableArrayList<Purchase>()

    @Bindable
    fun isFavoritesEmpty(): Boolean = favorites.isEmpty()

    @Bindable
    fun isFrequentsEmpty(): Boolean = frequents.isEmpty()

    init {
        activity.apply {
            etSearch.textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe { s ->
                        isSearching.set(s.isNotEmpty())

                        filteredSearchResults.clear()
                        filteredSearchResults += purchases.filter { it.readableName.contains(s, true) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreatePurchaseViewModel(this, realm)
        initData()
    }

    private fun initData() {
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.vm.favorites, BR.item)
                .type { Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal) }
                .map<ItemRealmProxy>(R.layout.item_purchase_item_horizontal)
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
//        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.vm.frequents, BR.item)
                .type { Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item) }
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
//        rvFrequents.setHasFixedSize(true)
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.vm.filteredSearchResults, BR.item)
                .type { Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item) }
                .into(rvSearchResults)

        rvFrequents.isNestedScrollingEnabled = false
//        rvFrequents.setHasFixedSize(true)
    }

    fun createProduct() {
        RxActivityResult.on(this)
                .startIntent(Intent(this, CreateProductActivity::class.java))
                .filter { it.resultCode() == Activity.RESULT_OK }
                .subscribe { result ->
                    with (Purchase().queryFirst { it.equalTo("id", result.data().getStringExtra(EXTRA_ID)) }!!) {
                        binding.vm.frequents.add(item!!)
                        if (item!!.isFavorite)
                            binding.vm.favorites.add(item)
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

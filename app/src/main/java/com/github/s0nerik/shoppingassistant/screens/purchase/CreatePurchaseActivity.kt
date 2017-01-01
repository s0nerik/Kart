package com.github.s0nerik.shoppingassistant.screens.purchase

import android.app.Activity
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_create_purchase.*

class CreatePurchaseViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm,
        val addProductViewModel: CreateProductViewModel
) {
    val VOICE_SEARCH_REQ_CODE = 672

    val isSearching: ObservableBoolean = ObservableBoolean(false)

    val frequents: RealmResults<Purchase> by lazy { frequentPurchases(realm) }
    val favorites: RealmResults<Purchase> by lazy { favoritePurchases(realm) }

    private val purchases: RealmResults<Purchase> by lazy { purchases(realm) }
    val filteredSearchResults: ObservableArrayList<Purchase> = ObservableArrayList()

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

    fun clearSearch(v: View) {
        activity.etSearch.setText("")
    }

    fun voiceSearch(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        activity.startActivityForResult(intent, VOICE_SEARCH_REQ_CODE)
    }

    fun notifyVoiceSearchResult(text: String) {
        activity.etSearch.setText(text)
    }
}

class CreateProductViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm
) : BaseObservable() {
    val isExpanded: ObservableBoolean = ObservableBoolean(false)

    private var category: Category? = null
    @Bindable
    fun getCategory() = category
    fun setCategory(c: Category) {
        category = c
        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
    }

    @Bindable
    fun getCategoryIconUrl(): String = category?.iconUrl.orEmpty()

    fun expand(v: View) {
        isExpanded.set(true)
    }

    fun shrink(v: View) {
        isExpanded.set(false)
    }
//    val frequents: RealmResults<Purchase> by lazy { frequentPurchases(realm) }
//    val favorites: RealmResults<Purchase> by lazy { favoritePurchases(realm) }
//
//    private val purchases: RealmResults<Purchase> by lazy { purchases(realm) }
//    val filteredSearchResults: ObservableArrayList<Purchase> = ObservableArrayList()

    init {
        Handler().postDelayed(
                {
                    setCategory(realm.where(Category::class.java).findFirst())
                }, 5000)
//        activity.apply {
////            spinnerSelectCategory.adapter =
//        }

//        activity.apply {
//            etSearch.textChanges()
//                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
//                    .subscribe { s ->
////                        isSearching.set(s.isNotEmpty())
////
////                        filteredSearchResults.clear()
////                        filteredSearchResults += purchases.filter { it.readableName.contains(s, true) }
//                    }
//        }
    }
}

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = CreatePurchaseViewModel(this, realm, CreateProductViewModel(this, realm))
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.viewModel.favorites, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase_horizontal)
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.viewModel.frequents, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.viewModel.filteredSearchResults, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvSearchResults)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == binding.viewModel.VOICE_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK) {
            data?.apply {
                binding.viewModel.notifyVoiceSearchResult(getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0])
            }
        }
    }
}

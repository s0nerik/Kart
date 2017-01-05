package com.github.s0nerik.shoppingassistant.screens.purchase

import android.app.Activity
import android.content.Intent
import android.databinding.*
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.*
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.model.Shop
import com.jakewharton.rxbinding.view.focusChanges
import com.jakewharton.rxbinding.widget.textChanges
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_create_purchase.*
import kotlinx.android.synthetic.main.card_create_product.*
import java.lang.ref.WeakReference
import java.util.*

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
    enum class Action { CREATE_PRODUCT, CREATE_CATEGORY, CREATE_SHOP }

    val isExpanded: ObservableBoolean = ObservableBoolean(false)

    private var category: Category? = null
    private var shop: Shop? = null
    private var action: Action = Action.CREATE_PRODUCT

    private lateinit var currentPopup: WeakReference<RelativePopupWindow>

    @Bindable
    fun getCategory() = category
    fun setCategory(c: Category) {
        category = c
        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
    }

    @Bindable
    fun getShop() = shop
    fun setShop(s: Shop) {
        shop = s
        notifyPropertyChanged(BR.shop)
        notifyPropertyChanged(BR.shopIconUrl)
    }

    @Bindable
    fun getAction() = action
    fun setAction(a: Action) {
        action = a
        notifyPropertyChanged(BR.action)
        activity.apply {
            val focusedText = when (a) {
                Action.CREATE_CATEGORY -> etNewCategoryName
                Action.CREATE_PRODUCT -> etNewProductName
                else -> null
            }
            focusedText?.requestFocus()
        }
    }

    @Bindable
    fun getCategoryIconUrl(): String = category?.iconUrl.orEmpty()

    @Bindable
    fun getShopIconUrl(): String = R.drawable.store.getDrawableUri(activity).toString()

    fun expand(v: View) {
        isExpanded.set(true)
    }

    fun shrink(v: View) {
        isExpanded.set(false)
    }

    fun selectCategory(v: View) {
        val categories = realm.where(Category::class.java).findAll()

        val binding = DataBindingUtil.inflate<PopupSelectProductCategoryBinding>(activity.layoutInflater, R.layout.popup_select_product_category, null, false)
        binding.viewModel = this

        val popup = RelativePopupWindow(binding.root, activity.btnSelectCategory.width, ViewGroup.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)

        currentPopup = WeakReference(popup)

        LastAdapter.with(categories, BR.item)
                .type {
                    Type<ItemCategoryBinding>(R.layout.item_category)
                            .onClick {
                                setCategory(item as Category)
                                currentPopup.safe { dismiss() }
                            }
                }
                .into(binding.recycler)
    }

    fun createCategory(v: View) {
        setAction(Action.CREATE_CATEGORY)
        currentPopup.safe { dismiss() }
        activity.etNewCategoryName.focusChanges()
                .filter { !it }
                .take(1)
                .bindUntilEvent(activity, ActivityEvent.DESTROY)
                .subscribe { setAction(Action.CREATE_PRODUCT) }
    }

    fun confirmCategotyCreation(v: View) {
        realm.executeTransaction {
            val category = realm.createObject(Category::class.java, Random().nextInt())
            category.name = activity.etNewCategoryName.text.toString()
            setCategory(category)
        }
        setAction(Action.CREATE_PRODUCT)
    }

    fun selectShop(v: View) {
        val shops = realm.where(Shop::class.java).findAll()

        val binding = DataBindingUtil.inflate<PopupSelectShopBinding>(activity.layoutInflater, R.layout.popup_select_shop, null, false)
        binding.viewModel = this

        val popup = RelativePopupWindow(binding.root, activity.btnSelectShop.width, ViewGroup.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)

        currentPopup = WeakReference(popup)

        LastAdapter.with(shops, BR.item)
                .type {
                    Type<ItemShopBinding>(R.layout.item_shop)
                            .onClick {
                                setShop(item as Shop)
                                currentPopup.safe { dismiss() }
                            }
                }
                .into(binding.recycler)
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

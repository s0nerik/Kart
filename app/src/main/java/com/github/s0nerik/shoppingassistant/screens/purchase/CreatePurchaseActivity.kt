package com.github.s0nerik.shoppingassistant.screens.purchase

import android.app.Activity
import android.content.Intent
import android.databinding.*
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.*
import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.model.Currency
import com.jakewharton.rxbinding.view.focusChanges
import com.jakewharton.rxbinding.widget.textChanges
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import io.realm.ItemRealmProxy
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_purchase.*
import kotlinx.android.synthetic.main.card_create_category.*
import kotlinx.android.synthetic.main.card_create_price.*
import kotlinx.android.synthetic.main.card_create_product.*
import kotlinx.android.synthetic.main.card_create_shop.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import java.util.*

class CreatePurchaseViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm,
        val addProductViewModel: CreateProductViewModel
) {
    val VOICE_SEARCH_REQ_CODE = 672

    val isSearching = ObservableBoolean(false)

    val frequents by lazy { frequentItems(realm) }
    val favorites by lazy { favoriteItems(realm) }
    private val purchases by lazy { purchases(realm) }

    val filteredSearchResults = ObservableArrayList<Purchase>()

    val favoritesSize: Int
        get() = favorites.size

    val frequentsSize: Int
        get() = frequents.size

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
}

class CreateProductViewModel(
        private val activity: CreatePurchaseActivity,
        private val realm: Realm
) : BaseObservable() {
    enum class Action { CREATE_PRODUCT, CREATE_PRICE, CREATE_CATEGORY, CREATE_SHOP }

    val isExpanded = ObservableBoolean(false)

    val pendingCurrency = ObservableField<Currency?>(null)

    private val itemCategory by lazy { realm.createObject(Category::class) }
    private val itemShop by lazy { realm.createObject(Shop::class) }
    private val itemPrice by lazy { realm.createObject(Price::class) }
    private val itemPriceChange by lazy { realm.createObject(PriceChange::class) }

    private var pendingItem = Item()

    private var action: Action = Action.CREATE_PRODUCT

    private lateinit var currentPopup: WeakReference<RelativePopupWindow>

    init {
        activity.etNewProductName
                .textChanges()
                .bindUntilEvent(activity, ActivityEvent.DESTROY)
                .subscribe {
                    val name = it.toString()
                    realm.executeTransaction {
                        pendingItem.name = name
                    }
                    notifyPropertyChanged(BR.item)
                }
    }

    //region Bindable properties
    @Bindable
    fun getCategory() = pendingItem.category
    fun setCategory(c: Category) {
        realm.executeTransaction {
            pendingItem.category = c
        }
        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getShop() = pendingItem.price?.shop
    fun setShop(s: Shop) {
        realm.executeTransaction {
            pendingItem.price?.shop = s

            if (pendingItem.price == null) {
                pendingItem.price = itemPrice
                pendingItem.price!!.shop = s
            } else if (pendingItem.price!!.isValid) {
                pendingItem.price!!.shop = s
            }
        }

        notifyPropertyChanged(BR.shop)
        notifyPropertyChanged(BR.shopIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getPrice() = pendingItem.price
    fun setPrice(p: Price) {
        realm.executeTransaction {
            pendingItem.price = p
        }
        notifyPropertyChanged(BR.price)
        notifyPropertyChanged(BR.priceIconUrl)
        notifyPropertyChanged(BR.item)
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
                Action.CREATE_PRICE -> etNewPriceValue
                else -> null
            }
            focusedText?.requestFocus()
            if (a != Action.CREATE_PRODUCT)
                focusedText?.focusChanges()
                        ?.filter { !it }
                        ?.take(1)
                        ?.bindUntilEvent(activity, ActivityEvent.DESTROY)
                        ?.subscribe { setAction(Action.CREATE_PRODUCT) }
        }
    }

    @Bindable
    fun getItem() = pendingItem
    fun setItem(i: Item) {
        pendingItem = i
        notifyPropertyChanged(BR._all)
    }

    @Bindable
    fun getCategoryIconUrl(): String = pendingItem.category?.iconUrl.orEmpty()

    @Bindable
    fun getShopIconUrl(): String = R.drawable.store.getDrawableUri(activity).toString()

    @Bindable
    fun getPriceIconUrl(): String = R.drawable.checkbox_blank_circle.getDrawableUri(activity).toString()
    //endregion

    fun expand(){
        if (!isExpanded.get()) {
            realm.executeTransaction {
                setItem(it.createObject(Item::class))
            }
            isExpanded.set(true)
        }
    }
    fun shrink() {
        shrink(false)
    }

    private fun shrink(shouldSave: Boolean) {
        if (isExpanded.get()) {
            if (!shouldSave)
                realm.executeTransaction { pendingItem.deleteFromRealm() }

            isExpanded.set(false)
        }
    }

    //region Price methods
    fun selectPrice() {
        setAction(Action.CREATE_PRICE)
    }

    fun selectCurrency() {
        val currencies = realm.where(Currency::class.java).findAll()

        val binding = DataBindingUtil.inflate<PopupSelectCurrencyBinding>(activity.layoutInflater, R.layout.popup_select_currency, null, false)
        binding.vm = this

        val popup = RelativePopupWindow(binding.root, activity.btnSelectCategory.width, ViewGroup.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.showOnAnchor(activity.btnCurrency, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)

        currentPopup = WeakReference(popup)

        LastAdapter.with(currencies, BR.item)
                .type {
                    Type<ItemCurrencyBinding>(R.layout.item_currency)
                            .onClick {
                                pendingCurrency.set(item as Currency)
                                currentPopup.safe { dismiss() }
                            }
                }
                .into(binding.recycler)
    }

    fun confirmPriceCreation() {
        val priceText = activity.etNewPriceValue.text.toString()
        if (priceText.isBlank()) {
            activity.toast("Price can't be blank!")
            return
        }

        val priceValue: Float
        try {
            priceValue = priceText.toFloat()
        } catch (e: NumberFormatException) {
            activity.toast("Wrong price format!")
            return
        }

        realm.executeTransaction {
            itemPriceChange.currency = pendingCurrency.get()
            itemPriceChange.date = Date()
            itemPriceChange.value = priceValue

            itemPrice.valueChanges.clear()
            itemPrice.valueChanges.add(itemPriceChange)
        }
        setPrice(itemPrice)

        // TODO: create Price if doesn't exist
        setAction(Action.CREATE_PRODUCT)
    }

    fun discardPriceCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Category methods
    fun selectCategory() {
        val categories = realm.where(Category::class.java).findAll()

        val binding = DataBindingUtil.inflate<PopupSelectProductCategoryBinding>(activity.layoutInflater, R.layout.popup_select_product_category, null, false)
        binding.vm = this

        val popup = RelativePopupWindow(binding.root, activity.btnSelectCategory.width, ViewGroup.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.showOnAnchor(activity.btnSelectCategory, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)

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

    fun createCategory() {
        setAction(Action.CREATE_CATEGORY)
        currentPopup.safe { dismiss() }
    }

    fun confirmCategoryCreation() {
        val name = activity.etNewCategoryName.text.toString()
        if (name.isEmpty()) {
            activity.toast("Category name can't be empty!")
            return
        }
        if (realm.where(Category::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("Category with the same name already exists!")
            return
        }

        realm.executeTransaction {
            itemCategory.name = activity.etNewCategoryName.text.toString()
        }
        setCategory(itemCategory)

        setAction(Action.CREATE_PRODUCT)
    }

    fun discardCategoryCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Shop methods
    fun selectShop() {
        val shops = realm.where(Shop::class.java).findAll()

        val binding = DataBindingUtil.inflate<PopupSelectShopBinding>(activity.layoutInflater, R.layout.popup_select_shop, null, false)
        binding.vm = this

        val popup = RelativePopupWindow(binding.root, activity.btnSelectShop.width, ViewGroup.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.showOnAnchor(activity.btnSelectShop, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)

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

    fun createShop() {
        setAction(Action.CREATE_SHOP)
        currentPopup.safe { dismiss() }
    }

    fun confirmShopCreation() {
        val name = activity.etNewShopName.text.toString()
        if (name.isEmpty()) {
            activity.toast("Shop name can't be empty!")
            return
        }
        if (realm.where(Shop::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("Shop with the same name already exists!")
            return
        }

        realm.executeTransaction {
            itemShop.name = name
        }
        setShop(itemShop)

        setAction(Action.CREATE_PRODUCT)
    }

    fun discardShopCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    fun createProduct() {
        val name = activity.etNewProductName.text.toString()
        if (name.isEmpty()) {
            activity.toast("Can't create a product without a name!")
            return
        }
        if (realm.where(Item::class.java).equalTo("name", name).findAllAsync().size > 0) {
            activity.toast("Product with the same name already exists!")
            return
        }
        if (pendingItem.category == null) {
            activity.toast("Category must be selected!")
            return
        }
        if (pendingItem.price?.shop == null) {
            activity.toast("Shop must be selected!")
            return
        }

        shrink(true)
    }
}

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreatePurchaseViewModel(this, realm, CreateProductViewModel(this, realm))
        initFavorites()
        initFrequents()
        initSearchResults()
    }

    private fun initFavorites() {
        LastAdapter.with(binding.vm.favorites, BR.item)
                .map<ItemRealmProxy>(R.layout.item_purchase_item_horizontal)
                .into(rvFavorites)

        rvFavorites.isNestedScrollingEnabled = false
        rvFavorites.setHasFixedSize(true)
    }

    private fun initFrequents() {
        LastAdapter.with(binding.vm.frequents, BR.item)
                .map<ItemRealmProxy>(R.layout.item_purchase_item)
                .into(rvFrequents)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }

    private fun initSearchResults() {
        LastAdapter.with(binding.vm.filteredSearchResults, BR.item)
                .map<ItemRealmProxy>(R.layout.item_purchase_item)
//                .map<PurchaseRealmProxy>(R.layout.item_purchase)
                .into(rvSearchResults)

        rvFrequents.isNestedScrollingEnabled = false
        rvFrequents.setHasFixedSize(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == binding.vm.VOICE_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK) {
            data?.apply {
                binding.vm.notifyVoiceSearchResult(getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0])
            }
        }
    }

    override fun onBackPressed() {
        binding.vm.addProductViewModel.shrink()
        super.onBackPressed()
    }
}

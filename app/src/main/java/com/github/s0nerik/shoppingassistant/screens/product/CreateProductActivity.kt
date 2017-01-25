package com.github.s0nerik.shoppingassistant.screens.product

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreateProductBinding
import com.github.s0nerik.shoppingassistant.getDrawableUri
import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.model.Currency
import com.github.s0nerik.shoppingassistant.safe
import com.jakewharton.rxbinding.view.focusChanges
import com.jakewharton.rxbinding.widget.textChanges
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.create
import com.vicpin.krealmextensions.query
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.card_create_category.*
import kotlinx.android.synthetic.main.card_create_price.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class CreateProductViewModel(
        private val activity: CreateProductActivity,
        private val realm: Realm
) : BaseObservable() {
    enum class Action { CREATE_PRODUCT, CREATE_PRICE, CREATE_CATEGORY, CREATE_SHOP }

    val isExpanded = ObservableBoolean(false)

    val pendingCurrency = ObservableField<Currency?>(null)

    private val itemCategory by lazy { Category() }
    private val itemShop by lazy { Shop() }
    private val itemPrice by lazy { Price() }
    private val itemPriceChange by lazy { PriceChange() }

    private val purchase by lazy { Purchase() }

    private var pendingItem = Item()

    private var action: Action = Action.CREATE_PRODUCT

    private lateinit var currentPopup: WeakReference<RelativePopupWindow>

    init {
        with(activity) {
            etProductName
                    .textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe {
                        val name = it.toString()
                        pendingItem.name = name
                        notifyPropertyChanged(BR.item)
                    }

//            spinnerQuantityQualifier.adapter = ArrayAdapter.createFromResource(activity, R.array.price_quantity_qualifiers, android.R.layout.simple_spinner_dropdown_item)
//            spinnerQuantityQualifier.itemSelections()
//                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
//                    .subscribe { i ->
//                        itemPriceChange.quantityQualifier = when (i) {
//                            0 -> PriceChange.QuantityQualifier.ITEM
//                            1 -> PriceChange.QuantityQualifier.KG
//                            else -> PriceChange.QuantityQualifier.ITEM
//                        }
//                    }
        }
    }

    //region Bindable properties
    @Bindable
    fun getCategory() = pendingItem.category
    fun setCategory(c: Category) {
        pendingItem.category = c
        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getShop() = pendingItem.price?.shop
    fun setShop(s: Shop) {
        pendingItem.price?.shop = s

        if (pendingItem.price == null) {
            pendingItem.price = itemPrice
            pendingItem.price!!.shop = s
        } else if (pendingItem.price!!.isValid) {
            pendingItem.price!!.shop = s
        }

        notifyPropertyChanged(BR.shop)
        notifyPropertyChanged(BR.shopIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getPrice() = pendingItem.price
    fun setPrice(p: Price) {
        pendingItem.price = p
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
//                Action.CREATE_PRODUCT -> etNewProductName
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
            setItem(Item())
            isExpanded.set(true)
        }
    }
    fun shrink() {
        shrink(false)
    }

    private fun shrink(shouldSave: Boolean) {
        if (isExpanded.get()) {
            if (shouldSave)
                purchase.create()

            isExpanded.set(false)
        }
    }

    //region Price methods
    fun selectPrice() {
        setAction(Action.CREATE_PRICE)
    }

    fun selectCurrency() {
//        val currencies = realm.where(Currency::class.java).findAll()
//
//        val binding = DataBindingUtil.inflate<PopupSelectCurrencyBinding>(activity.layoutInflater, R.layout.popup_select_currency, null, false)
//        binding.vm = this
//
//        val popup = RelativePopupWindow(binding.root, activity.btnSelectCategory.width, ViewGroup.LayoutParams.WRAP_CONTENT)
//        popup.isOutsideTouchable = true
//        popup.showOnAnchor(activity.btnCurrency, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)
//
//        currentPopup = WeakReference(popup)
//
//        LastAdapter.with(currencies, BR.item)
//                .type {
//                    Type<ItemCurrencyBinding>(R.layout.item_currency)
//                            .onClick {
//                                pendingCurrency.set(item as Currency)
//                                currentPopup.safe { dismiss() }
//                            }
//                }
//                .into(binding.recycler)
    }

    fun confirmPriceCreation() {
        val priceText = ""
//        val priceText = activity.etNewPriceValue.text.toString()
//        if (priceText.isBlank()) {
//            activity.toast("Price can't be blank!")
//            return
//        }
//
//        val priceValue: Float
//        try {
//            priceValue = priceText.toFloat()
//        } catch (e: NumberFormatException) {
//            activity.toast("Wrong price format!")
//            return
//        }
//
//        itemPriceChange.currency = pendingCurrency.get()
//        itemPriceChange.date = Date()
//        itemPriceChange.value = priceValue
//
//        itemPrice.valueChanges.clear()
//        itemPrice.valueChanges.add(itemPriceChange)
//        setPrice(itemPrice)
//
//        // TODO: create Price if doesn't exist
//        setAction(Action.CREATE_PRODUCT)
    }

    fun discardPriceCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Category methods
    fun selectCategory() {
//        val categories = realm.where(Category::class.java).findAll()
//
//        val binding = DataBindingUtil.inflate<PopupSelectProductCategoryBinding>(activity.layoutInflater, R.layout.popup_select_product_category, null, false)
//        binding.vm = this
//
//        val popup = RelativePopupWindow(binding.root, activity.btnSelectCategory.width, ViewGroup.LayoutParams.WRAP_CONTENT)
//        popup.isOutsideTouchable = true
//        popup.showOnAnchor(activity.btnSelectCategory, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)
//
//        currentPopup = WeakReference(popup)
//
//        LastAdapter.with(categories, BR.item)
//                .type {
//                    Type<ItemCategoryBinding>(R.layout.item_category)
//                            .onClick {
//                                setCategory(item as Category)
//                                currentPopup.safe { dismiss() }
//                            }
//                }
//                .into(binding.recycler)
    }

    fun createCategory() {
        setAction(Action.CREATE_CATEGORY)
        currentPopup.safe { dismiss() }
    }

    fun confirmCategoryCreation() {
//        val name = activity.etNewCategoryName.text.toString()
//        if (name.isEmpty()) {
//            activity.toast("Category name can't be empty!")
//            return
//        }
//        if (realm.where(Category::class.java).equalTo("name", name).findFirst() != null) {
//            activity.toast("Category with the same name already exists!")
//            return
//        }
//
//        itemCategory.name = activity.etNewCategoryName.text.toString()
//        setCategory(itemCategory)
//
//        setAction(Action.CREATE_PRODUCT)
    }

    fun discardCategoryCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Shop methods
    fun selectShop() {
//        val shops = realm.where(Shop::class.java).findAll()
//
//        val binding = DataBindingUtil.inflate<PopupSelectShopBinding>(activity.layoutInflater, R.layout.popup_select_shop, null, false)
//        binding.vm = this
//
//        val popup = RelativePopupWindow(binding.root, activity.btnSelectShop.width, ViewGroup.LayoutParams.WRAP_CONTENT)
//        popup.isOutsideTouchable = true
//        popup.showOnAnchor(activity.btnSelectShop, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)
//
//        currentPopup = WeakReference(popup)
//
//        LastAdapter.with(shops, BR.item)
//                .type {
//                    Type<ItemShopBinding>(R.layout.item_shop)
//                            .onClick {
//                                setShop(item as Shop)
//                                currentPopup.safe { dismiss() }
//                            }
//                }
//                .into(binding.recycler)
    }

    fun createShop() {
        setAction(Action.CREATE_SHOP)
        currentPopup.safe { dismiss() }
    }

    fun confirmShopCreation() {
//        val name = activity.etNewShopName.text.toString()
//        if (name.isEmpty()) {
//            activity.toast("Shop name can't be empty!")
//            return
//        }
//        if (realm.where(Shop::class.java).equalTo("name", name).findFirst() != null) {
//            activity.toast("Shop with the same name already exists!")
//            return
//        }
//
//        itemShop.name = name
//        setShop(itemShop)
//
//        setAction(Action.CREATE_PRODUCT)
    }

    fun discardShopCreation() {
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    fun editName() {
        activity.etProductName.requestFocus()
    }

    fun purchaseProduct() {
        val name = ""
//        val name = activity.etNewProductName.text.toString()
        if (name.isEmpty()) {
            activity.toast("Can't create a product without a name!")
            return
        }
        if (Item().query { it.equalTo("name", name) }.isNotEmpty()) {
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

        purchase.item = pendingItem
        purchase.date = Date()
        // TODO: add UI for selection the amount
        purchase.amount = 1.toFloat()

        shrink(true)
    }
}

class CreateProductActivity : BaseBoundActivity<ActivityCreateProductBinding>(R.layout.activity_create_product) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreateProductViewModel(this, realm)
    }
}
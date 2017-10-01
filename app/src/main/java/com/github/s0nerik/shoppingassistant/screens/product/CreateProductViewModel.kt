package com.github.s0nerik.shoppingassistant.screens.product

import android.app.Activity
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.support.design.widget.BottomSheetDialogFragment
import android.view.inputmethod.InputMethodManager
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.getDrawablePath
import com.github.s0nerik.shoppingassistant.model.*
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.create
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.saveManaged
import io.realm.Realm
import kotlinx.android.synthetic.main.item_purchase_preview.view.*
import kotlinx.android.synthetic.main.view_create_category.*
import kotlinx.android.synthetic.main.view_create_product.*
import kotlinx.android.synthetic.main.view_create_shop.*
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.toast
import java.util.*

class CreateProductViewModel(
    private val activity: CreateProductActivity,
    private val realm: Realm
) : BaseObservable() {
    enum class Action { CREATE_PRODUCT, CREATE_PRICE, SELECT_CATEGORY, CREATE_CATEGORY, SELECT_SHOP, CREATE_SHOP }

    val pendingCurrency = ObservableField<Currency>(MainPrefs.defaultCurrency)
    val pendingPriceText = ObservableField<String>("")

    private var itemCategory = RealmCategory()
    private var itemShop = RealmShop()
    private val itemPrice by lazy { RealmPriceHistory() }
    val itemPriceChange by lazy {
        val priceChange = RealmPrice()
        priceChange.date = Date()
        priceChange
    }

    private val purchase by lazy { RealmPurchase() }
    private var pendingItem = RealmItem()
    private var action: Action = Action.CREATE_PRODUCT

    private var bottomSheet: BottomSheetDialogFragment? = null

    init {
        if (pendingItem.priceHistory == null) {
            pendingItem.priceHistory = itemPrice
            pendingItem.priceHistory?.values?.add(itemPriceChange)
        }

        activity.apply {
            preview.title
                .textChanges()
                .map { it.toString() }
                .bindUntilEvent(activity, ActivityEvent.DESTROY)
                .subscribe { setName(it) }
        }
    }

    //region Bindable properties
    @Bindable
    fun getCategory() = pendingItem.category
    fun setCategory(c: RealmCategory) {
        pendingItem.category = c
        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getShop() = pendingItem.priceHistory?.shop
    fun setShop(s: RealmShop) {
        pendingItem.priceHistory?.shop = s

        if (pendingItem.priceHistory == null) {
            pendingItem.priceHistory = itemPrice
            pendingItem.priceHistory!!.shop = s
        } else if (pendingItem.priceHistory!!.isValid) {
            pendingItem.priceHistory!!.shop = s
        }

        notifyPropertyChanged(BR.shop)
        notifyPropertyChanged(BR.shopIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getPrice() = pendingItem.priceHistory
    fun setPrice(p: RealmPriceHistory) {
        pendingItem.priceHistory = p
        notifyPropertyChanged(BR.price)
        notifyPropertyChanged(BR.priceIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getAction() = action
    fun setAction(a: Action) {
        when(a) {
            Action.SELECT_SHOP -> {
                if (action != Action.CREATE_SHOP) {
                    val sheet = SelectShopBottomSheet(this)
                    sheet.show(activity.supportFragmentManager, null)
                    bottomSheet = sheet
                }
            }
            Action.SELECT_CATEGORY -> {
                if (action != Action.CREATE_CATEGORY) {
                    val sheet = SelectCategoryBottomSheet(this)
                    sheet.show(activity.supportFragmentManager, null)
                    bottomSheet = sheet
                }
            }
            Action.CREATE_PRICE -> {
                if (action != Action.CREATE_PRICE) {
                    val sheet = SelectPriceBottomSheet(this)
                    sheet.show(activity.supportFragmentManager, null)
                    bottomSheet = sheet
                }
            }
            Action.CREATE_PRODUCT -> bottomSheet?.dismiss()
        }

        action = a
        notifyPropertyChanged(BR.action)
        activity.apply {
            val focusedText = when (a) {
                Action.CREATE_CATEGORY -> etNewCategoryName
//                Action.CREATE_PRODUCT -> etNewProductName
//                Action.CREATE_PRICE -> etNewPriceValue
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
    fun setItem(i: RealmItem) {
        pendingItem = i
        notifyPropertyChanged(BR._all)
    }

    @Bindable
    fun getCategoryIconUrl(): String = pendingItem.category?.iconUrl.orEmpty()

    @Bindable
    fun getShopIconUrl(): String = R.drawable.store.getDrawablePath(activity)

    @Bindable
    fun getPriceIconUrl(): String = R.drawable.checkbox_blank_circle.getDrawablePath(activity)

    @Bindable
    fun getFavoriteIconUrl(): String =
        if (pendingItem.isFavorite) R.drawable.product_fav_yes.getDrawablePath(activity) else R.drawable.product_fav_no.getDrawablePath(activity)

    @Bindable
    fun isFavorite(): Boolean = pendingItem.isFavorite

    @Bindable
    fun isPriceSet(): Boolean = itemPriceChange.value != null
    //endregion

    fun setName(name: String) {
        pendingItem.name = name
        notifyPropertyChanged(BR.item)
        with(activity.preview.title) { setSelection(text.length) }
    }

    fun close() {
        close(false)
    }

    private fun close(shouldSave: Boolean) {
        if (shouldSave) {
            purchase.create()
            activity.setResult(Activity.RESULT_OK, Intent().putExtra(CreateProductActivity.EXTRA_ID, purchase.id))
        } else {
            activity.setResult(Activity.RESULT_CANCELED)
        }

        activity.finish()
    }

    //region RealmPriceHistory methods
    fun selectPrice() {
        setAction(Action.CREATE_PRICE)
    }

    fun selectCurrency() {
        SelectCurrencyBottomSheet(this).show(activity.supportFragmentManager, null)
    }

    fun confirmPendingPrice() {
        val priceText = pendingPriceText.get()
        if (priceText.isBlank()) {
            activity.toast("RealmPriceHistory can't be blank!")
            return
        }

        val priceValue: Float
        try {
            priceValue = priceText.toFloat()
        } catch (e: NumberFormatException) {
            activity.toast("Wrong priceHistory format!")
            return
        }

        itemPriceChange.currency = pendingCurrency.get()
        itemPriceChange.date = Date()
        itemPriceChange.value = priceValue

        itemPrice.values.clear()
        itemPrice.values.add(itemPriceChange)
        setPrice(itemPrice)

        // TODO: create RealmPriceHistory if doesn't exist
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region RealmCategory methods
    fun selectCategory() {
        setAction(Action.SELECT_CATEGORY)
    }

    fun toggleCategoryCreation() {
        if (action == Action.SELECT_CATEGORY) {
            setAction(Action.CREATE_CATEGORY)
        } else {
            setAction(Action.SELECT_CATEGORY)
        }
    }

    fun confirmCategoryCreation() {
        val name = bottomSheet!!.etNewCategoryName.text.toString()
        if (name.isEmpty()) {
            activity.toast("RealmCategory name can't be empty!")
            return
        }
        if (realm.where(RealmCategory::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("RealmCategory with the same name already exists!")
            return
        }

        itemCategory.name = name
        val cat = itemCategory.saveManaged(realm)
        setCategory(cat)

        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region RealmShop methods
    fun selectShop() {
        setAction(Action.SELECT_SHOP)
    }

    fun toggleShopCreation() {
        if (action == Action.SELECT_SHOP) {
            setAction(Action.CREATE_SHOP)
        } else {
            setAction(Action.SELECT_SHOP)
        }
    }

    fun confirmShopCreation() {
        val name = bottomSheet!!.etNewShopName.text.toString()
        if (name.isEmpty()) {
            activity.toast("RealmShop name can't be empty!")
            return
        }
        if (realm.where(RealmShop::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("RealmShop with the same name already exists!")
            return
        }

        itemShop.name = name
        realm.executeTransaction {
            itemShop = realm.copyToRealm(itemShop)
        }

        setShop(itemShop)

        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    fun editName() {
        activity.apply {
            if (preview.title.requestFocus()) {
                inputMethodManager.showSoftInput(preview.title, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun toggleFavorite() {
        pendingItem.isFavorite = !pendingItem.isFavorite
        notifyPropertyChanged(BR.favorite)
        notifyPropertyChanged(BR.favoriteIconUrl)
    }

    fun purchaseProduct() {
        val name = activity.preview.title.text.toString()
        if (name.isEmpty()) {
            activity.toast("Can't create a product without a name!")
            return
        }
        if (RealmItem().query { it.equalTo("name", name) }.isNotEmpty()) {
            activity.toast("Product with the same name already exists!")
            return
        }
        if (pendingItem.category == null) {
            activity.toast("RealmCategory must be selected!")
            return
        }
        if (pendingItem.priceHistory?.shop == null) {
            activity.toast("RealmShop must be selected!")
            return
        }

        purchase.item = pendingItem
        purchase.date = Date()
        // TODO: add UI for selection the amount
        purchase.amount = 1.toFloat()

        close(true)
    }
}
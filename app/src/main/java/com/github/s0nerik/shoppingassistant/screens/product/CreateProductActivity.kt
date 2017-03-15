package com.github.s0nerik.shoppingassistant.screens.product

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreateProductBinding
import com.github.s0nerik.shoppingassistant.getDrawablePath
import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.model.Currency
import com.jakewharton.rxbinding.view.focusChanges
import com.jakewharton.rxbinding.widget.itemSelections
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.create
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.saveManaged
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.item_purchase_preview.view.*
import kotlinx.android.synthetic.main.view_create_category.*
import kotlinx.android.synthetic.main.view_create_product.*
import kotlinx.android.synthetic.main.view_create_shop.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

const val EXTRA_ID = "id"

class CreateProductViewModel(
        private val activity: CreateProductActivity,
        private val realm: Realm
) : BaseObservable() {
    enum class Action { CREATE_PRODUCT, CREATE_PRICE, SELECT_CATEGORY, CREATE_CATEGORY, SELECT_SHOP, CREATE_SHOP }

    val pendingCurrency = ObservableField<Currency?>(null)

    private var itemCategory = Category()
    private var itemShop = Shop()
    private val itemPrice by lazy { Price() }
    private val itemPriceChange by lazy {
        val priceChange = PriceChange()
        priceChange.date = Date()
        priceChange
    }

    private val purchase by lazy { Purchase() }
    private var pendingItem = Item()
    private var action: Action = Action.CREATE_PRODUCT

    private var bottomSheet: BottomSheetDialogFragment? = null

    init {
        activity.apply {
            preview.title
                    .textChanges()
                    .map { it.toString() }
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe { setName(it) }

            etNewPriceValue
                    .textChanges()
                    .map { it.toString() }
                    .map { if (!it.isNullOrBlank()) it.toFloat() else null }
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe {
                        if (pendingItem.price == null) {
                            pendingItem.price = itemPrice
                            pendingItem.price?.valueChanges?.add(itemPriceChange)
                        }
                        itemPriceChange.value = it
                        notifyPropertyChanged(BR.item)
                        notifyPropertyChanged(BR.priceSet)
                    }

            spinnerQuantityQualifier.adapter = ArrayAdapter.createFromResource(activity, R.array.price_quantity_qualifiers, android.R.layout.simple_spinner_dropdown_item)
            spinnerQuantityQualifier.itemSelections()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe { i ->
                        itemPriceChange.quantityQualifier = when (i) {
                            0 -> PriceChange.QuantityQualifier.ITEM
                            1 -> PriceChange.QuantityQualifier.KG
                            else -> PriceChange.QuantityQualifier.ITEM
                        }
                    }
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
            Action.CREATE_PRODUCT -> bottomSheet?.dismiss()
        }

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
            activity.setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ID, purchase.id))
        } else {
            activity.setResult(Activity.RESULT_CANCELED)
        }

        activity.finish()
    }

    //region Price methods
    fun selectPrice() {
        setAction(Action.CREATE_PRICE)
    }

    fun selectCurrency() {
        SelectCurrencyBottomSheet(this).show(activity.supportFragmentManager, null)
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

        itemPriceChange.currency = pendingCurrency.get()
        itemPriceChange.date = Date()
        itemPriceChange.value = priceValue

        itemPrice.valueChanges.clear()
        itemPrice.valueChanges.add(itemPriceChange)
        setPrice(itemPrice)

        // TODO: create Price if doesn't exist
        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Category methods
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
            activity.toast("Category name can't be empty!")
            return
        }
        if (realm.where(Category::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("Category with the same name already exists!")
            return
        }

        itemCategory.name = name
        val cat = itemCategory.saveManaged(realm)
        setCategory(cat)

        setAction(Action.CREATE_PRODUCT)
    }
    //endregion

    //region Shop methods
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
            activity.toast("Shop name can't be empty!")
            return
        }
        if (realm.where(Shop::class.java).equalTo("name", name).findFirst() != null) {
            activity.toast("Shop with the same name already exists!")
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

        close(true)
    }
}

class CreateProductActivity : BaseBoundActivity<ActivityCreateProductBinding>(R.layout.activity_create_product) {
    companion object {
        fun intent(ctx: Context, productName: String? = null): Intent {
            val intent = Intent(ctx, CreateProductActivity::class.java)
            intent.putExtra("name", productName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreateProductViewModel(this, realm)

        val extraName = intent.getStringExtra("name")
        extraName?.let { binding.vm.setName(it.capitalize()) }

        animateAppear()
    }

    private fun animateAppear() {
        val rightButtons = arrayOf(btnSelectName, btnSelectPrice, btnSelectCategory, btnSelectShop)
        val leftButtons = arrayOf(btnFavorite)
        val bottomButtons = arrayOf(btnCancelPurchase, btnPurchaseNewProduct)

        val previewAppearDuration = 500L
        val previewAppearDelay = 200L
        val buttonsAppearOffset = previewAppearDuration + previewAppearDelay

        root.alpha = 0f
        root.animate()
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()

        preview.alpha = 0f
        preview.animate()
                .alpha(1f)
                .setDuration(previewAppearDuration)
                .setStartDelay(previewAppearDelay)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()

        rightButtons.forEachIndexed { i, v ->
            v.translationX = dip(-48).toFloat()
            v.alpha = 0f
            v.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(200)
                    .setStartDelay(buttonsAppearOffset + i * 50L)
                    .start()
        }

        leftButtons.forEachIndexed { i, v ->
            v.translationX = dip(48).toFloat()
            v.alpha = 0f
            v.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(200)
                    .setStartDelay(buttonsAppearOffset + i * 50L + rightButtons.size * 50L)
                    .start()
        }

        bottomButtons.forEachIndexed { i, v ->
            v.translationY = dip(-24).toFloat()
            v.alpha = 0f
            v.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(200)
                    .setStartDelay(buttonsAppearOffset + i * 50L + rightButtons.size * 50L + leftButtons.size * 50L)
                    .start()
        }
    }

    override fun finish() {
        disappearAndFinish()
    }

    private fun disappearAndFinish() {
        val animators = mutableListOf<Animator>()

        val rightButtons = arrayOf(btnSelectName, btnSelectPrice, btnSelectCategory, btnSelectShop).reversed()
        val leftButtons = arrayOf(btnFavorite).reversed()
        val bottomButtons = arrayOf(btnCancelPurchase, btnPurchaseNewProduct).reversed()

        val getDelay = { i: Int -> i * 50L }

        bottomButtons.forEachIndexed { i, v ->
            animators += ViewPropertyObjectAnimator
                    .animate(v)
                    .translationY(dip(-24).toFloat())
                    .alpha(0f)
                    .setDuration(200)
                    .setStartDelay(getDelay(i))
                    .get()
        }

        leftButtons.forEachIndexed { i, v ->
            animators += ViewPropertyObjectAnimator
                    .animate(v)
                    .translationX(dip(48).toFloat())
                    .alpha(0f)
                    .setDuration(200)
                    .setStartDelay(getDelay(i + bottomButtons.size))
                    .get()
        }

        rightButtons.forEachIndexed { i, v ->
            animators += ViewPropertyObjectAnimator
                    .animate(v)
                    .translationX(dip(-48).toFloat())
                    .alpha(0f)
                    .setDuration(200)
                    .setStartDelay(getDelay(i + bottomButtons.size + leftButtons.size))
                    .get()
        }

        animators += ViewPropertyObjectAnimator
                .animate(preview)
                .alpha(0f)
                .setDuration(500L)
                .setInterpolator(FastOutSlowInInterpolator())
                .setStartDelay(getDelay(rightButtons.size + bottomButtons.size + leftButtons.size))
                .get()

        animators += ViewPropertyObjectAnimator
                .animate(root)
                .alpha(0f)
                .setDuration(500L)
                .setInterpolator(FastOutSlowInInterpolator())
                .setStartDelay(getDelay(rightButtons.size + bottomButtons.size + leftButtons.size) + 200L)
                .get()

        val set = AnimatorSet()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super@CreateProductActivity.finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
                super@CreateProductActivity.finish()
            }
        })
        set.playTogether(animators)
        set.start()
    }

}
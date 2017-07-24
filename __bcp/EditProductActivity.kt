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
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreateProductBinding
import com.github.s0nerik.shoppingassistant.getDrawablePath
import com.github.s0nerik.shoppingassistant.model.*
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.vicpin.krealmextensions.*
import io.reactivex.Maybe
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.item_purchase_preview.view.*
import kotlinx.android.synthetic.main.view_create_category.*
import kotlinx.android.synthetic.main.view_create_product.*
import kotlinx.android.synthetic.main.view_create_shop.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.toast
import rx_activity_result2.RxActivityResult
import java.util.*

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

const val EXTRA_ID = "id"

class CreateProductViewModel(
        private val activity: EditProductActivity,
        private val realm: Realm,
        private val itemToEdit: Item? = null
) : BaseObservable() {
    enum class Action { EDIT_PRODUCT, CREATE_PRICE, SELECT_CATEGORY, CREATE_CATEGORY, SELECT_SHOP, CREATE_SHOP }

    val pendingCurrency = ObservableField<Currency>(MainPrefs.defaultCurrency)
    val pendingPriceText = ObservableField<String>("")

    private val pendingItem: Item by lazy {
        itemToEdit ?: with(Item()) {
            priceHistory = PriceHistory()

            this
        }
    }
    private var action: Action = Action.EDIT_PRODUCT

    private var bottomSheet: BottomSheetDialogFragment? = null

    init {
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
    fun setCategory(c: Category) {
        pendingItem.category = c

        notifyPropertyChanged(BR.category)
        notifyPropertyChanged(BR.categoryIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getShop() = pendingItem.priceHistory?.shop
    fun setShop(s: Shop) {
        pendingItem.priceHistory!!.shop = s

        notifyPropertyChanged(BR.shop)
        notifyPropertyChanged(BR.shopIconUrl)
        notifyPropertyChanged(BR.item)
    }

    @Bindable
    fun getPrice() = pendingItem.priceHistory!!.values.last()
    fun setPrice(p: Price) {
        pendingItem.priceHistory!!.values.add(p)

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
            Action.EDIT_PRODUCT -> bottomSheet?.dismiss()
        }

        action = a
        notifyPropertyChanged(BR.action)
        activity.apply {
            val focusedText = when (a) {
                Action.CREATE_CATEGORY -> etNewCategoryName
//                Action.EDIT_PRODUCT -> etNewProductName
//                Action.CREATE_PRICE -> etNewPriceValue
                else -> null
            }
            focusedText?.requestFocus()
            if (a != Action.EDIT_PRODUCT)
                focusedText?.focusChanges()
                        ?.filter { !it }
                        ?.take(1)
                        ?.bindUntilEvent(activity, ActivityEvent.DESTROY)
                        ?.subscribe { setAction(Action.EDIT_PRODUCT) }
        }
    }

    @Bindable
    fun getItem() = pendingItem

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
            pendingItem.createOrUpdate()
            activity.setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ID, pendingItem.id))
        } else {
            activity.setResult(Activity.RESULT_CANCELED)
        }

        activity.finish()
    }

    //region PriceHistory methods
    fun selectPrice() {
        setAction(Action.CREATE_PRICE)
    }

    fun selectCurrency() {
        SelectCurrencyBottomSheet(this).show(activity.supportFragmentManager, null)
    }

    fun confirmPendingPrice() {
        val priceText = pendingPriceText.get()
        if (priceText.isBlank()) {
            activity.toast("PriceHistory can't be blank!")
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

        setPrice(itemPrice)

        setAction(Action.EDIT_PRODUCT)
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

        setAction(Action.EDIT_PRODUCT)
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

        setAction(Action.EDIT_PRODUCT)
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

    fun confirm() {
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
        if (pendingItem.priceHistory?.shop == null) {
            activity.toast("Shop must be selected!")
            return
        }

        close(true)
    }
}

class EditProductActivity : BaseBoundActivity<ActivityCreateProductBinding>(R.layout.activity_edit_product) {
    companion object {
        private fun intent(ctx: Context, productName: String? = null, productId: String? = null): Intent {
            val intent = Intent(ctx, EditProductActivity::class.java)
            intent.putExtra("name", productName)
            intent.putExtra("id", productId)
            return intent
        }

        fun create(a: Activity, searchQuery: String = ""): Maybe<Item> {
            return RxActivityResult.on(a)
                    .startIntent(intent(a, searchQuery))
                    .firstElement()
                    .filter { it.resultCode() == Activity.RESULT_OK }
                    .map { result -> Item().queryFirst { it.equalTo("id", result.data().getStringExtra(EXTRA_ID)) }!! }
        }

        fun edit(a: Activity, item: Item): Maybe<Item> {
            return RxActivityResult.on(a)
                    .startIntent(intent(a, null, item.id))
                    .firstElement()
                    .filter { it.resultCode() == Activity.RESULT_OK }
                    .map { result -> Item().queryFirst { it.equalTo("id", result.data().getStringExtra(EXTRA_ID)) }!! }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = CreateProductViewModel(this, realm)

        val extraName = intent.getStringExtra("name")
        extraName?.let { binding.vm!!.setName(it.capitalize()) }

        animateAppear()
    }

    private fun animateAppear() {
        val rightButtons = arrayOf(btnSelectName, btnSelectPrice, btnSelectCategory, btnSelectShop)
        val leftButtons = arrayOf(btnFavorite)
        val bottomButtons = arrayOf(btnCancel, btnConfirm)

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
        preview.translationY = dip(48).toFloat()
        preview.animate()
                .alpha(1f)
                .translationY(0f)
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
        val bottomButtons = arrayOf(btnCancel, btnConfirm).reversed()

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
                .translationY(dip(48).toFloat())
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
            override fun onAnimationEnd(animation: Animator?) = super@EditProductActivity.finish()
            override fun onAnimationCancel(animation: Animator?) = super@EditProductActivity.finish()
        })
        set.playTogether(animators)
        set.start()
    }

}
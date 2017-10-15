package com.github.s0nerik.shoppingassistant.screens.product

import android.databinding.Bindable
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.getDrawablePath
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Price
import com.github.s0nerik.shoppingassistant.model.Shop
import com.github.s0nerik.shoppingassistant.utils.weak

class CreateProductViewModel : BaseViewModel() {
    var interactor by weak<CreateProductViewModelInteractor>()

    var name: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    var category: Category? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.category)
        }

    val isCategorySet: Boolean
        @Bindable("category") get() = category != null

    val readableCategory: String
        @Bindable("category") get() = category?.name ?: "Uncategorized"

    var price: Price? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.price)
        }

    val isPriceSet: Boolean
        @Bindable("price") get() = price != null

    val readablePrice: String
        @Bindable("price") get() = price?.toString() ?: "Unknown price"

    var shop: Shop? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.shop)
        }

    val isShopSet: Boolean
        @Bindable("shop") get() = shop != null

    val readableShop: String
        @Bindable("shop") get() = shop?.name ?: "Unknown shop"

    var isFavorite: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.favorite)
        }

    val favoriteIconUrl: String
        @Bindable("favorite") get() =
            if (isFavorite) R.drawable.product_fav_yes.getDrawablePath()
            else R.drawable.product_fav_no.getDrawablePath()

    val categoryIconUrl: String
        @Bindable("category") get() = category?.iconUrl.orEmpty()

    val priceIconUrl: String
        @Bindable("price") get() = R.drawable.cash_multiple.getDrawablePath()

    val shopIconUrl: String
        @Bindable("shop") get() = R.drawable.store.getDrawablePath()

    fun selectPrice() {
        interactor!!.selectPrice()
                .takeUntilCleared()
                .subscribe { price = it }
    }

    fun selectCategory() {
        interactor!!.selectCategory()
                .takeUntilCleared()
                .subscribe { category = it }
    }

    fun selectShop() {
        interactor!!.selectShop()
                .takeUntilCleared()
                .subscribe { shop = it }
    }

    fun toggleFavorite() {
        TODO()
    }

    fun save() {
        TODO()
    }

    fun cancel() {
        interactor!!.finishWithResult(null)
    }
}
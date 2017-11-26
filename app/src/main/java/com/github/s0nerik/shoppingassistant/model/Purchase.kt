package com.github.s0nerik.shoppingassistant.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Purchase(
        val id: String = Db.randomUuidString(),
        val item: Item,
        val date: Date,
        val amount: Float = 1f
) : Parcelable {
    val readableName: String
        get() = item.readableName
    val readablePrice: String
        get() = item.priceHistory!!.getPriceString(date, true, amount)
    val readablePriceDefaultCurrency: String
        get() {
            val priceString = item.priceHistory!!.getPriceString(date, true, amount, true)
            return if (priceString != getString(R.string.price_unknown)) "($priceString)" else ""
        }
    val readableCategory: String
        get() = item.readableCategory
    val readableShop: String
        get() = item.readableShop
    val readableAmount: String
        get() {
            val priceChange = item.priceHistory!!.getPriceChangeForDate(date)
            priceChange?.quantityQualifier = Price.QuantityQualifier.ITEM // TODO()

            return if (amount > 1f && priceChange != null)
                when (priceChange.quantityQualifier) {
                    Price.QuantityQualifier.ITEM -> "%.0f".format(amount)
                    Price.QuantityQualifier.KG -> "${"%.1f".format(amount)} kg"
                }
            else ""
        }
    val iconUrl: String
        get() = item.iconUrl
    val price: Price
        get() = item.priceHistory.getPriceForDate(date)!!
    val fullPrice: Float
        get() = amount.times(price.value ?: 0f)
    val isInDefaultCurrency: Boolean
        get() = price.currency == MainPrefs.defaultCurrency
    val priceInDefaultCurrency: Float
        get() = price.convertedTo(MainPrefs.defaultCurrency).value ?: 0f

    fun confirm() = MainRepository.save(this)
    fun remove() = MainRepository.delete(this)

    companion object {
        fun from(p: RealmPurchase): Purchase {
            return Purchase(p.id, Item.from(p.item!!), p.date!!, p.amount)
        }
    }
}

open class RealmPurchase(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var item: RealmItem? = null,
        open var date: Date? = null,
        open var amount: Float = 1f
) : RealmObject() {
    companion object {
        fun from(p: Purchase): RealmPurchase {
            return RealmPurchase(p.id, RealmItem.from(p.item), p.date, p.amount)
        }
    }
}

@SuppressLint("ParcelCreator")
@Parcelize
open class FuturePurchase(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var item: Item? = null,
        open var creationDate: Date? = null,
        open var lastUpdate: Date? = null,
        open var amount: Float = 1f
) : Parcelable {
    companion object {
        fun from(p: RealmFuturePurchase): FuturePurchase {
            return FuturePurchase(p.id, Item.from(p.item!!), p.creationDate, p.lastUpdate, p.amount)
        }
    }
}

open class RealmFuturePurchase(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var item: RealmItem? = null,
        open var creationDate: Date? = null,
        open var lastUpdate: Date? = null,
        open var amount: Float = 1f
) : RealmObject() {
    companion object {
        fun from(p: FuturePurchase): RealmFuturePurchase {
            return RealmFuturePurchase(p.id, RealmItem.from(p.item!!), p.creationDate, p.lastUpdate, p.amount)
        }
    }
}
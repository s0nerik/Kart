package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
import com.github.s0nerik.shoppingassistant.randomUuidString
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.save
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Purchase(
        @PrimaryKey open var id: String = randomUuidString(),
        open var item: Item? = null,
        open var date: Date? = null,
        open var amount: Float = 1f
) : RealmObject() {
    val readableName: String
        get() = item!!.readableName
    val readablePrice: String
        get() = item!!.priceHistory!!.getPriceString(date!!, true, amount)
    val readablePriceDefaultCurrency: String
        get() {
            val priceString = item!!.priceHistory!!.getPriceString(date!!, true, amount, true)
            return if (priceString != getString(R.string.price_unknown)) "($priceString)" else ""
        }
    val readableCategory: String
        get() = item!!.readableCategory
    val readableShop: String
        get() = item!!.readableShop
    val readableAmount: String
        get() {
            val priceChange = item!!.priceHistory!!.getPriceChangeForDate(date!!)
            return if (amount > 1f && priceChange != null)
                when (priceChange.quantityQualifier) {
                    Price.QuantityQualifier.ITEM -> "%.0f".format(amount)
                    Price.QuantityQualifier.KG -> "${"%.1f".format(amount)} kg"
                }
            else ""
        }
    val iconUrl: String
        get() = item!!.iconUrl
    val price: Price
        get() = item!!.priceHistory!!.getPriceForDate(date!!)
    val fullPrice: Float
        get() = amount.times(price.value ?: 0f)
    val isInDefaultCurrency: Boolean
        get() = price.currencyCode == MainPrefs.defaultCurrencyCode
    val priceInDefaultCurrency: Float
        get() = price.convertedTo(MainPrefs.defaultCurrency).value ?: 0f
}

open class FuturePurchase(
        @PrimaryKey open var id: String = randomUuidString(),
        open var item: Item? = null,
        open var creationDate: Date? = null,
        open var lastUpdate: Date? = null,
        open var amount: Float = 1f
) : RealmObject() {
    fun confirm() {
        Purchase(id, item, Date(), amount).save()
    }

    fun remove() {
        this.delete { it.equalTo("id", id) }
    }
}
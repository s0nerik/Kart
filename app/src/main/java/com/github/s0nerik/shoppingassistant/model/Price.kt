package com.github.s0nerik.shoppingassistant.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
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
data class Price(
        val id: String = Db.randomUuidString(),
        val value: Float? = null,
        val date: Date = Date(),
        var currencyCode: String = MainPrefs.defaultCurrencyCode,
        var quantityQualifierName: String = Price.QuantityQualifier.ITEM.name
) : Parcelable {
    enum class QuantityQualifier { ITEM, KG }

//    constructor(
//            id: String = Db.randomUuidString(),
//            value: Float? = null,
//            date: Date = Date(),
//            currency: Currency = MainPrefs.defaultCurrency,
//            quantityQualifier: QuantityQualifier = Price.QuantityQualifier.ITEM
//    ) : this(id, value, date, currency.currencyCode, quantityQualifier.name)

    fun convertedTo(currency: Currency): Price {
        if (currencyCode == MainPrefs.defaultCurrencyCode) return this
        return Price(id, Db.exchangedValue(value!!, this.currency, currency, date), date, currency.currencyCode, quantityQualifierName)
    }

    var currency: Currency
        get() = Currency.getInstance(currencyCode)
        set(value) {
            currencyCode = value.currencyCode
        }

    var quantityQualifier: QuantityQualifier
        get() = QuantityQualifier.valueOf(quantityQualifierName)
        set(value) {
            quantityQualifierName = value.name
        }

    override fun toString(): String {
        return value?.let { getString(R.string.price_with_currency_fmt, currency.displayName, it) } ?: "Unknown price"
    }

    companion object {
        fun from(e: RealmPrice): Price {
            return Price(e.id, e.value, e.date, e.currencyCode, e.quantityQualifierName)
        }
    }
}

open class RealmPrice(
        @PrimaryKey var id: String = Db.randomUuidString(),
        var value: Float? = null,
        var date: Date = Date(),
        var currencyCode: String = MainPrefs.defaultCurrencyCode,
        var quantityQualifierName: String = Price.QuantityQualifier.ITEM.name
) : RealmObject() {
    companion object {
        fun from(e: Price): RealmPrice {
            return RealmPrice(e.id, e.value, e.date, e.currency.currencyCode, e.quantityQualifier.name)
        }
    }
}
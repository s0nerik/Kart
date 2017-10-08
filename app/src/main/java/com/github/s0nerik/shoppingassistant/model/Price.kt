package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
data class Price(
        val id: String = Db.randomUuidString(),
        val value: Float? = null,
        val date: Date = Date(),
        private var currencyCode: String = MainPrefs.defaultCurrencyCode,
        private var quantityQualifierName: String = Price.QuantityQualifier.ITEM.name
) {
    enum class QuantityQualifier { ITEM, KG }

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
        var currencyCode: String = "",
        var quantityQualifierName: String = ""
) : RealmObject() {
    companion object {
        fun from(e: Price): RealmPrice {
            return RealmPrice(e.id, e.value, e.date, e.currency.currencyCode, e.quantityQualifier.name)
        }
    }
}
package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.exchangedValue
import com.github.s0nerik.shoppingassistant.randomUuidString
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Price(
        @PrimaryKey open var id: String = randomUuidString(),
        open var value: Float? = null,
        open var currencyCode: String = MainPrefs.defaultCurrencyCode,
        open var date: Date = Date(),
        open var quantityQualifierName: String = Price.QuantityQualifier.ITEM.name
) : RealmObject() {
    enum class QuantityQualifier { ITEM, KG }

    fun convertedTo(currency: Currency): Price {
        if (currencyCode == MainPrefs.defaultCurrencyCode) return this
        return Price(id, exchangedValue(value!!, this.currency, currency, date), currency.currencyCode, date, quantityQualifierName)
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
}
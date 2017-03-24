package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
import com.github.s0nerik.shoppingassistant.randomUuidString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class PriceHistory(
        @PrimaryKey open var id: String = randomUuidString(),
        open var shop: Shop? = null,
        open var values: RealmList<Price> = RealmList()
) : RealmObject() {
    val currentValueString: String
        get() = getPriceString(Date(), true)

    fun getPriceChangeForDate(date: Date): Price? {
        if (values.size == 1) {
            return values[0]
        } else if (values.size > 0) {
            values.sortedBy { it.date }.forEach {
                if (date < it.date!!) {
                    return it
                }
            }
        }
        return null
    }

    fun getPriceForDate(date: Date): Price {
        var price: Price = Price()
        if (values.size > 0) {
            values.sortedBy { it.date }.forEach {
                if (date < it.date!!) {
                    return it
                }
                price = it
            }
        }
        return price
    }

    fun getPriceString(date: Date, withCurrency: Boolean, amount: Float = 1f): String {
        val price = getPriceForDate(date)
        if (price.value == null) {
            return getString(R.string.price_unknown)
        } else if (withCurrency && price.currency != null) {
            return getString(R.string.price_with_currency_fmt, price.currency!!.sign, price.value!! * amount)
        } else {
            return getString(R.string.price_no_currency_fmt, price.value!! * amount)
        }
    }
}
package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.getString
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
open class Price(
        @PrimaryKey open var id: String = randomUuidString(),
        open var shop: Shop? = null,
        open var valueChanges: RealmList<PriceChange> = RealmList()
) : RealmObject() {
    val currentValueString: String
        get() = getPrice(Date(), true)

    private fun getPriceForDate(date: Date, amount: Float = 1f): Pair<Float?, Currency?> {
        var price: Float? = null
        var currency: Currency? = null
        if (valueChanges.size > 0) {
            valueChanges.sortedBy { it.date }.forEach {
                if (date < it.date!!) {
                    return Pair(price, currency)
                }
                price = it.value
                currency = it.currency
            }
        }
        return Pair(price?.times(amount), currency)
    }

    fun getPrice(date: Date, withCurrency: Boolean, amount: Float = 1f): String {
        val price = getPriceForDate(date, amount)
        if (price.first == null) {
            return getString(R.string.price_unknown)
        } else if (withCurrency && price.second != null) {
            return getString(R.string.price_with_currency_fmt, price.second!!.sign, price.first!!)
        } else {
            return getString(R.string.price_no_currency_fmt, price.first!!)
        }
    }
}
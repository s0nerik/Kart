package com.github.s0nerik.shoppingassistant.model

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
        @PrimaryKey open var id: Long = 0,
        open var shop: Shop? = null,
        open var valueChanges: RealmList<PriceChange> = RealmList()
) : RealmObject() {
    val currentValueString: String
        get() = "%.2f".format(getPriceForDate(Date()).first)

    fun getPriceForDate(date: Date, amount: Int = 1): Pair<Float?, Currency?> {
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

    fun getPriceWithCurrency(date: Date, amount: Int = 1): String {
        val price = getPriceForDate(date)
        return "%s%.2f".format(price.second?.sign!!, price.first!!)
    }
}
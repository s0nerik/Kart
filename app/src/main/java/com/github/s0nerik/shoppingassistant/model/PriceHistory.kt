package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
data class PriceHistory(
        val id: String,
        val shop: Shop?,
        val values: List<Price>
) {
    companion object {
        fun from(e: RealmPriceHistory): PriceHistory {
            return PriceHistory(e.id, Shop.from(e.shop!!), e.values.map { Price.from(it) })
        }
    }
}

open class RealmPriceHistory(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var shop: RealmShop? = null,
        open var values: RealmList<RealmPrice> = RealmList()
) : RealmObject() {
    val currentValueString: String
        get() = getPriceString(Date(), true)

    fun getPriceChangeForDate(date: Date): RealmPrice? {
        if (values.size == 1) {
            return values[0]
        } else if (values.size > 0) {
            values.sortedBy { it.date }.forEach {
                if (date < it.date) {
                    return it
                }
            }
        }
        return null
    }

    fun getPriceForDate(date: Date): RealmPrice {
        var price: RealmPrice = RealmPrice()
        if (values.size > 0) {
            values.sortedBy { it.date }.forEach {
                if (date < it.date) {
                    return it
                }
                price = it
            }
        }
        return price
    }

    fun getPriceString(date: Date, withCurrency: Boolean, amount: Float = 1f, convertToDefaultCurrency: Boolean = false): String {
        var price = getPriceForDate(date)
        if (convertToDefaultCurrency) price = price.convertedTo(MainPrefs.defaultCurrency)
        if (price.value == null) {
            return getString(R.string.price_unknown)
        } else if (withCurrency) {
            return getString(R.string.price_with_currency_fmt, price.currency.symbol, price.value!! * amount)
        } else {
            return getString(R.string.price_no_currency_fmt, price.value!! * amount)
        }
    }
}
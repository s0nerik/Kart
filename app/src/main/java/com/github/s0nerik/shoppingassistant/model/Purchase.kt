package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.randomUuidString
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
        get() = if (amount > 1) "${amount}x ${item!!.readableName}" else item!!.readableName
    val readablePrice: String
        get() = item!!.price!!.getPriceString(date!!, true, amount)
    val readableCategory: String
        get() = item!!.readableCategory
    val readableShop: String
        get() = item!!.readableShop
    val iconUrl: String
        get() = item!!.iconUrl
    val price: PricePair
        get() = item!!.price!!.getPriceForDate(date!!, amount)

    // TODO: provide a way of conversion between the currencies
    val priceLocal: Float
        get() = price.first ?: 0f
}
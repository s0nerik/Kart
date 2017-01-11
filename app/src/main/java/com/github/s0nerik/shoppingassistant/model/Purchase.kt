package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Purchase(
        @PrimaryKey open var id: String = "",
        open var item: Item? = null,
        open var date: Date? = null,
        open var amount: Int = 0
) : RealmObject() {
    val readableName: String
        get() = if (amount > 1) "${amount}x ${item!!.readableName}" else item!!.readableName
    val readablePrice: String
        get() = item!!.price!!.getPriceWithCurrency(date!!, amount)
    val readableCategory: String
        get() = item!!.readableCategory
    val readableShop: String
        get() = item!!.readableShop
    val iconUrl: String
        get() = item!!.iconUrl
}
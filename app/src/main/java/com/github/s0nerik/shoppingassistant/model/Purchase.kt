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
        @PrimaryKey open var id: Long = 0,
        open var item: Item? = null,
        open var date: Date? = null,
        open var amount: Int = 0
) : RealmObject() {
    val readableName
        get() = item!!.name
    val readablePrice
        get() = "${amount}x ${item!!.price!!.getPriceWithCurrency(date!!)}"
    val readableCategory
        get() = item!!.category!!.name
    val readableShop
        get() = item!!.price!!.shop!!.name
    val iconUrl
        get() = item!!.category!!.iconUrl
}
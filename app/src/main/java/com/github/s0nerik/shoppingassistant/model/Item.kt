package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Item(
        @PrimaryKey open var id: Long = 0,
        open var name: String = "",
        open var category: Category? = null,
        open var price: Price? = null
) : RealmObject() {
    val readableName: String
        get() = name
    val readablePrice: String
        get() = price!!.getPriceWithCurrency(Date(), 1)
    val readableCategory: String
        get() = category!!.name
    val readableShop: String
        get() = price!!.shop!!.name
    val iconUrl: String
        get() = category!!.iconUrl
}
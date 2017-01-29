package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.randomUuidString
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Item(
        @PrimaryKey open var id: String = randomUuidString(),
        open var name: String = "",
        open var category: Category? = null,
        open var price: Price? = null
) : RealmObject() {
    val readableNamePreview: String
        get() = if (name.isNotBlank()) name else ""
    val readableName: String
        get() = if (name.isNotBlank()) name else "Unnamed"
    val readablePrice: String
        get() = if (price != null) price!!.currentValueString else "Unknown price"
    val readableCategory: String
        get() = if (category != null) category!!.name else "Uncategorized"
    val readableShop: String
        get() = if (price != null && price!!.shop != null) price!!.shop!!.name else "Unknown shop"
    val iconUrl: String
        get() = if (category != null) category!!.iconUrl else ""
}
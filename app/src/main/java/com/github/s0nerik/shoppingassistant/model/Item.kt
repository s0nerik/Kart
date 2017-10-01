package com.github.s0nerik.shoppingassistant.model


import com.github.s0nerik.shoppingassistant.Db
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
data class Item(
        val id: String,
        val name: String,
        val category: Category?,
        val priceHistory: PriceHistory?,
        val isFavorite: Boolean
) {
    companion object {
        fun from(i: RealmItem): Item {
            return Item(i.id, i.name, Category.from(i.category!!), PriceHistory.from(i.priceHistory!!), i.isFavorite)
        }
    }
}

open class RealmItem(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var name: String = "",
        open var category: RealmCategory? = null,
        open var priceHistory: RealmPriceHistory? = null,
        open var isFavorite: Boolean = false
) : RealmObject() {
    val readableNamePreview: String
        get() = if (name.isNotBlank()) name else ""
    val readableName: String
        get() = if (name.isNotBlank()) name else "Unnamed"
    val readablePrice: String
        get() = if (priceHistory != null) priceHistory!!.currentValueString else "Unknown priceHistory"
    val readableCategory: String
        get() = if (category != null) category!!.name else "Uncategorized"
    val readableShop: String
        get() = if (priceHistory != null && priceHistory!!.shop != null) priceHistory!!.shop!!.name else "Unknown shop"
    val iconUrl: String
        get() = if (category != null) category!!.iconUrl else ""
}
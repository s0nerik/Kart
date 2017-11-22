package com.github.s0nerik.shoppingassistant.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.s0nerik.shoppingassistant.Db
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Item(
        val id: String = Db.randomUuidString(),
        val name: String = "",
        val category: Category? = null,
        val priceHistory: PriceHistory = PriceHistory(),
        val isFavorite: Boolean = false
) : Parcelable {
    val readableNamePreview: String
        get() = if (name.isNotBlank()) name else ""
    val readableName: String
        get() = if (name.isNotBlank()) name else "Unnamed"
    val readablePrice: String
        get() = priceHistory.currentValueString
    val readableCategory: String
        get() = category!!.name
    val readableShop: String
        get() = if (priceHistory.shop != null) priceHistory.shop.name else "Unknown shop"
    val iconUrl: String
        get() = category!!.iconUrl

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
    companion object {
        fun from(i: Item): RealmItem {
            return RealmItem(i.id, i.name, RealmCategory.from(i.category!!), RealmPriceHistory.from(i.priceHistory), i.isFavorite)
        }
    }
}
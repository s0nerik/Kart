package com.github.s0nerik.shoppingassistant.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.s0nerik.shoppingassistant.Db
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Category(
        val id: String = Db.randomUuidString(),
        val name: String = "",
        val iconUrl: String = ""
) : Parcelable {
    companion object {
        fun from(e: RealmCategory): Category {
            return Category(e.id, e.name, e.iconUrl)
        }
    }
}

open class RealmCategory(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var name: String = "",
        open var iconUrl: String = ""
) : RealmObject() {
    companion object {
        fun from(e: Category): RealmCategory {
            return RealmCategory(e.id, e.name, e.iconUrl)
        }
    }
}
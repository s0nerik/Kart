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
data class Shop(
        val id: String = Db.randomUuidString(),
        val name: String
) : Parcelable {
   companion object {
        fun from(e: RealmShop): Shop {
            return Shop(e.id, e.name)
        }
    }
}

open class RealmShop(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var name: String = ""
) : RealmObject() {
    companion object {
        fun from(e: Shop): RealmShop {
            return RealmShop(e.id, e.name)
        }
    }
}
package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.Db
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
data class Shop(
        val id: String,
        val name: String
) {
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
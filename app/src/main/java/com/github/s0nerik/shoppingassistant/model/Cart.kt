package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.vicpin.krealmextensions.save
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 2/24/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Cart(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var date: Date = Date(),
        open var purchases: RealmList<Purchase> = RealmList()
) : RealmObject() {
    companion object {
        val purchases = observableListOf<Purchase>()
        fun add(purchase: Purchase) {
            purchases.add(purchase)
        }
        fun remove(purchase: Purchase) {
            purchases.remove(purchase)
        }
        fun save() {
            val cart = Cart()
            cart.purchases.addAll(purchases)
            cart.save()
            purchases.clear()
        }
        fun clear() {
            purchases.clear()
        }
        fun isEmpty(): Boolean {
            return purchases.size == 0
        }
    }
}
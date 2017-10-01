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
data class Cart(
        val id: String,
        val date: Date,
        var purchases: List<Purchase>
) {
    companion object {
        fun from(c: RealmCart): Cart {
            return Cart(c.id, c.date, c.purchases.map { Purchase.from(it) })
        }
    }
}

open class RealmCart(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var date: Date = Date(),
        open var purchases: RealmList<RealmPurchase> = RealmList()
) : RealmObject() {
    companion object {
        val purchases = observableListOf<RealmPurchase>()
        fun add(purchase: RealmPurchase) {
            purchases.add(purchase)
        }
        fun add(item: RealmItem, date: Date = Date()) {
            add(RealmPurchase(item = item, date = date))
        }
        @JvmStatic
        fun remove(purchase: RealmPurchase) {
            purchases.remove(purchase)
        }
        fun save() {
            val cart = RealmCart()
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
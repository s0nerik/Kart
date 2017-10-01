package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.Db
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.ext.realmListOf
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
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
        val id: String = Db.randomUuidString(),
        val date: Date = Date(),
        var purchases: MutableList<Purchase> = mutableListOf()
) {
    companion object {
        val purchases = observableListOf<Purchase>()
        fun add(purchase: Purchase) {
            purchases.add(purchase)
        }
        fun add(item: Item, date: Date = Date()) {
            add(Purchase(item = item, date = date))
        }
        @JvmStatic
        fun remove(purchase: Purchase) {
            purchases.remove(purchase)
        }
        fun save() {
            val cart = Cart()
            cart.purchases.addAll(purchases)
            MainRepository.save(cart)
            purchases.clear()
        }
        fun clear() {
            purchases.clear()
        }
        fun isEmpty(): Boolean {
            return purchases.size == 0
        }

        fun from(c: RealmCart): Cart {
            return Cart(c.id, c.date, c.purchases.map { Purchase.from(it) }.toMutableList())
        }
    }
}

open class RealmCart(
        @PrimaryKey open var id: String,
        open var date: Date,
        open var purchases: RealmList<RealmPurchase> = RealmList()
) : RealmObject() {
    companion object {
        fun from(c: Cart): RealmCart {
            return RealmCart(c.id, c.date, realmListOf(c.purchases.map { RealmPurchase.from(it) }))
        }
    }
}
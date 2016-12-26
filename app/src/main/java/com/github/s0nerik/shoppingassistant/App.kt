package com.github.s0nerik.shoppingassistant

import android.app.Application
import com.github.s0nerik.shoppingassistant.model.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import khronos.days
import khronos.minus
import khronos.minutes

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(
                RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
        )
        createDummyPurchases()
    }

    private fun createDummyPurchases() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.deleteAll()

            (0..10).forEach { i ->
                val purchase = it.createObject(Purchase::class.java, i)
                purchase.amount = 1

                val date = i.days.ago
                purchase.date = date

                val category = it.createObject(Category::class.java, i)
                category.name = i.toString()

                val shop = it.createObject(Shop::class.java, i)
                shop.name = "$i Shop"

                val currency = it.createObject(Currency::class.java, i)
                currency.name = i.toString()
                currency.sign = "$"

                val priceChange = it.createObject(PriceChange::class.java, i)
                priceChange.date = date - 1.minutes
                priceChange.value = i.toFloat()
                priceChange.currency = currency

                val price = it.createObject(Price::class.java, i)
                price.shop = shop
                price.valueChanges = RealmList(priceChange)

                val item = it.createObject(Item::class.java, i)
                item.category = category
                item.price = price

                purchase.item = item
            }
        }
        realm.close()
    }
}
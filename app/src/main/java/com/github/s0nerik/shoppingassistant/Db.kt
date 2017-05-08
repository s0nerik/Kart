package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.s0nerik.shoppingassistant.model.ExchangeRates
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Realm
import io.realm.RealmModel
import io.realm.Sort
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Alex on 12/29/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

object Db {
    fun initDatabase(ctx: Context, isDebug: Boolean = false, removeOldData: Boolean = false, dummyShops: Boolean = false, dummyCategories: Boolean = false, dummyPurchases: Boolean = false) {
        Realm.getDefaultInstance().use {
            if (isDebug && removeOldData)
                it.executeTransaction(Realm::deleteAll)

            if (isDebug) {
                if (dummyShops) createDummyShops(ctx, it)
                if (dummyCategories) createDummyCategories(ctx, it)
                if (dummyPurchases && dummyShops && dummyCategories) createDummyPurchases(ctx, it)
            }
        }
    }

    fun purchases(realm: Realm): List<Purchase> {
        return realm.where(Purchase::class.java).findAll()
    }

    fun items(realm: Realm): List<Item> {
        return realm.where(Item::class.java).findAll()
    }

    fun favoriteItems(realm: Realm): List<Item> {
        return realm.where(Item::class.java).equalTo("isFavorite", true).findAll()
    }

    fun frequentItems(realm: Realm): List<Item> {
        val purchases = realm.where(Purchase::class.java).findAll()

        return purchases
                .map { it.item!! }
                .distinct()
                .sortedByDescending { purchases.where().equalTo("item.id", it.id).count() }
                .take(10)
    }

    fun recentPurchases(realm: Realm, fromDate: Date? = null): List<Purchase> {
        var query = realm.where(Purchase::class.java)
        fromDate?.let { query = query.greaterThan("date", it) }
        return query.findAllSorted("date", Sort.DESCENDING)
    }

    fun randomUuidString(): String {
        return UUID.randomUUID().toString()
    }

    fun exchangedValue(sourceValue: Float, sourceCurrency: Currency, targetCurrency: Currency, date: Date): Float? {
        Realm.getDefaultInstance().use {
            val ratesContainer = it.where(ExchangeRates::class.java).findAll().minBy { date.time - it.date.time } ?: return null

            val sourceRate = if (sourceCurrency.currencyCode == ratesContainer.sourceCurrencyCode) {
                1f
            } else {
                ratesContainer.rates.find { it.currencyCode == sourceCurrency.currencyCode }?.value
            }
            val targetRate = if (targetCurrency.currencyCode == ratesContainer.sourceCurrencyCode) {
                1f
            } else {
                ratesContainer.rates.find { it.currencyCode == targetCurrency.currencyCode }?.value
            }

            if (sourceRate == null || targetRate == null) return null

            return sourceValue / sourceRate * targetRate
        }
    }
}

fun <E : RealmModel> Realm.createObject(clazz: KClass<E>): E {
    return createObject(clazz.java, Db.randomUuidString())
}
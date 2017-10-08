package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.s0nerik.shoppingassistant.FakeDataProvider.createDummyPurchases
import com.github.s0nerik.shoppingassistant.FakeDataProvider.createDummyShops
import com.github.s0nerik.shoppingassistant.model.*
import io.realm.Realm
import io.realm.RealmModel
import io.realm.Sort
import org.json.JSONArray
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Alex on 12/29/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

object Db {
    private val ctx: Context
        get() = App.context

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    fun initDatabase(ctx: Context, isDebug: Boolean = false, removeOldData: Boolean = false, dummyShops: Boolean = false, dummyCategories: Boolean = false, dummyPurchases: Boolean = false) {
        realm.use {
            if (isDebug && removeOldData)
                it.executeTransaction(Realm::deleteAll)

            createBasicCategories()

            if (isDebug) {
                if (dummyShops) createDummyShops(it)
                if (dummyPurchases && dummyShops && dummyCategories) createDummyPurchases(it)
            }
        }
    }

    private fun createBasicCategories() {
        realm.use {
            it.executeTransaction {
                val categories = JSONArray(ctx.resources.openRawResource(R.raw.categories).bufferedReader().use { it.readText() })
                for (i in 0..(categories.length() - 1)) {
                    val category = categories.getJSONObject(i)
                    val name = category.getString("name")
                    val iconId = ctx.resources.getIdentifier(category.getString("icon"), "drawable", ctx.packageName)
                    createOrReturnCategory(it, name, iconId.getDrawablePath())
                }
            }
        }
    }

    fun createOrReturnCategory(realm: Realm, name: String, iconUrl: String? = null): RealmCategory {
        val presentCategory = realm.where(RealmCategory::class.java).equalTo("name", name).findFirst()
        if (presentCategory != null) return presentCategory

        val category = realm.createObject(RealmCategory::class)
        category.name = name
        if (iconUrl != null) category.iconUrl = iconUrl

        return category
    }

    val purchases: List<Purchase>
        get() = realm.where(RealmPurchase::class.java).findAll().map { Purchase.from(it) }

    val items: List<Item>
        get() = realm.where(RealmItem::class.java).findAll().map { Item.from(it) }

    val favoriteItems: List<Item>
        get() = realm.where(RealmItem::class.java)
                .equalTo("isFavorite", true)
                .findAll()
                .map { Item.from(it) }

    val frequentItems: List<Item>
        get() {
            val purchases = realm.where(RealmPurchase::class.java).findAll()

            return purchases
                    .map { Item.from(it.item!!) }
                    .distinct()
                    .sortedByDescending { purchases.where().equalTo("item.id", it.id).count() }
                    .take(10)
        }

    fun recentPurchases(fromDate: Date? = null): List<RealmPurchase> {
        var query = realm.where(RealmPurchase::class.java)
        fromDate?.let { query = query.greaterThan("date", it) }
        return query.findAllSorted("date", Sort.DESCENDING)
    }

    fun moneySpent(fromDate: Date = Date(0)): Double {
        return Db.recentPurchases(fromDate).map { Purchase.from(it) }.sumByDouble { it.fullPrice.toDouble() }
    }

    fun statsDistribution(fromDate: Date = Date(0)): Map<Category?, List<Purchase>> {
        return purchases
                .filter { it.date!! >= fromDate }
                .groupBy { it.item?.category }
    }

    fun randomUuidString(): String {
        return UUID.randomUUID().toString()
    }

    fun exchangedValue(sourceValue: Float, sourceCurrency: Currency, targetCurrency: Currency, date: Date): Float? {
        realm.use {
            val ratesContainer = it.where(RealmExchangeRates::class.java).findAll().minBy { date.time - it.date.time } ?: return null

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
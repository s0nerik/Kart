package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.s0nerik.shoppingassistant.model.Currency
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Realm
import io.realm.RealmModel
import io.realm.Sort
import rx.Observable
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Alex on 12/29/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun initDatabase(ctx: Context, isDebug: Boolean = false, removeOldData: Boolean = false, dummyShops: Boolean = false, dummyCategories: Boolean = false, dummyPurchases: Boolean = false) {
    Realm.getDefaultInstance().use {
        if (isDebug && removeOldData)
            it.executeTransaction(Realm::deleteAll)

        createCurrenciesIfNeeded(it)

        if (isDebug) {
            if (dummyShops) createDummyShops(ctx, it)
            if (dummyCategories) createDummyCategories(ctx, it)
            if (dummyPurchases && dummyShops && dummyCategories) createDummyPurchases(ctx, it)
        }
    }
}

fun createCurrenciesIfNeeded(realm: Realm) {
    val currencies = java.util.Currency.getAvailableCurrencies()

    if (realm.where(Currency::class.java).findFirst() == null) {
        // TODO: replace with async transaction when fake data is no longer needed
        realm.executeTransaction {
            currencies.forEach {
                val currency = realm.createObject(Currency::class.java, it.currencyCode)
                currency.sign = it.symbol
                currency.name = it.displayName
            }
        }
    }
}

fun purchases(realm: Realm): List<Purchase> {
    return realm.where(Purchase::class.java).findAll()
}

fun purchasesObservable(realm: Realm): Observable<out List<Purchase>> {
    return realm.where(Purchase::class.java).findAll().asObservable()
}

fun items(realm: Realm): List<Item> {
    return realm.where(Item::class.java).findAll()
}

fun itemsObservable(realm: Realm): Observable<out List<Item>> {
    return realm.where(Item::class.java).findAll().asObservable()
}

fun favoriteItems(realm: Realm): List<Item> {
    return realm.where(Item::class.java).equalTo("isFavorite", true).findAll()
}

fun favoriteItemsObservable(realm: Realm): Observable<out List<Item>> {
    return realm.where(Item::class.java).equalTo("isFavorite", true).findAll().asObservable()
}

fun frequentItems(realm: Realm): List<Item> {
    val purchases = realm.where(Purchase::class.java).findAll()

    return purchases
            .map { it.item!! }
            .distinct()
            .sortedByDescending { purchases.where().equalTo("item.id", it.id).count() }
            .take(10)
}

fun frequentItemsObservable(realm: Realm): Observable<out List<Item>> {
    return realm.where(Purchase::class.java)
            .findAll()
            .asObservable()
            .map { items ->
                items.map { it.item!! }
                        .distinct()
                        .sortedByDescending { items.where().equalTo("item.id", it.id).count() }
                        .take(10)
            }
}

fun recentPurchases(realm: Realm, fromDate: Date? = null): List<Purchase> {
    val query = realm.where(Purchase::class.java)
    fromDate?.let { query.between("date", it, Date()) }
    return query.findAllSorted("date", Sort.DESCENDING)
}

fun recentPurchasesObservable(realm: Realm): Observable<out List<Purchase>> {
    return realm.where(Purchase::class.java).findAllSorted("date", Sort.DESCENDING).asObservable()
}

fun <E : RealmModel> Realm.createObject(clazz: KClass<E>): E {
    return createObject(clazz.java, UUID.randomUUID().toString())
}

fun randomUuidString(): String {
    return UUID.randomUUID().toString()
}
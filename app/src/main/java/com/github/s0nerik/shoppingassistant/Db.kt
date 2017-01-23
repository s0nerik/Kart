package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.s0nerik.shoppingassistant.model.Currency
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Alex on 12/29/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun initDatabase(ctx: Context, isDebug: Boolean) {
    Realm.getDefaultInstance().use {
        if (isDebug)
            it.executeTransaction(Realm::deleteAll)

        createCurrenciesIfNeeded(it)
        createDummyPurchases(ctx, it)
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

fun purchases(realm: Realm): RealmResults<Purchase> {
    return realm.where(Purchase::class.java).findAll()
}

// TODO: implement this by creating a new purchase for a favorite product
fun favoritePurchases(realm: Realm): RealmResults<Purchase> {
    return purchases(realm)
}

// TODO: implement this by creating a new purchase for a frequently bought product
fun frequentPurchases(realm: Realm): RealmResults<Purchase> {
    return purchases(realm)
}

// TODO: implement this by creating a new purchase for a favorite product
fun favoriteItems(realm: Realm): RealmResults<Item> {
    return realm.where(Item::class.java).findAll()
}

// TODO: implement this by creating a new purchase for a frequently bought product
fun frequentItems(realm: Realm): RealmResults<Item> {
    return realm.where(Item::class.java).findAll()
}

// TODO: implement this by creating a new purchase for a recently bought product
fun recentPurchases(realm: Realm): RealmResults<Purchase> {
    return realm.where(Purchase::class.java).findAll()
}

fun <E : RealmModel> Realm.createObject(clazz: KClass<E>): E {
    return createObject(clazz.java, UUID.randomUUID().toString())
}

fun randomUuidString(): String {
    return UUID.randomUUID().toString()
}
package com.github.s0nerik.shoppingassistant

import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.Realm
import io.realm.RealmResults

/**
 * Created by Alex on 12/29/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

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
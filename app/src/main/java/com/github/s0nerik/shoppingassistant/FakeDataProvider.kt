package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.debop.kodatimes.ago
import com.github.debop.kodatimes.days
import com.github.debop.kodatimes.minutes
import com.github.debop.kodatimes.now
import com.github.s0nerik.shoppingassistant.model.*
import io.realm.Realm
import io.realm.RealmList
import org.joda.time.DateTime
import java.util.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun createDummyPurchases(ctx: Context, realm: Realm) {
    realm.executeTransaction {
        createPurchases(it, "Food & Drinks", "Potato", "Meat", "Milk", "Fish", "Tomatoes", "Vodka", "Wine", "Tea")
        createPurchases(it, "Clothes", "Pants", "Sweater", "Coat", "Scarf", "Shirt", "Trousers", "Boots", "Helmet")
        createPurchases(it, "Computers & Peripherals", "Mouse", "Keyboard", "Headphones")
        createPurchases(it, "Software", "IntelliJ IDEA", "ReSharper")
        createPurchases(it, "Games", "Silent Hill", "FIFA 17", "Mortal Kombat X")
        createPurchases(it, "Music", "Master Of Puppets", "Ungrateful")
        createPurchases(it, "Household", "Vacuum cleaner", "Teapot", "Cup")
        createPurchases(it, "Smartphones & Accessories", "Samsung Galaxy S8", "Xiaomi Mi Mix", "HTC 11")
        createPurchases(it, "Gifts", "Barbie", "Lego")

        createPurchaseLists(it)
    }
}

private fun createPurchases(realm: Realm, category: String, vararg names: String) {
    names.forEach { name ->
        providePurchase(realm, name, category)
    }
}

private fun createPurchaseLists(realm: Realm) {
    val items = mutableListOf<Item>()
    items += realm.where(Item::class.java).findAll()
    for (i in 0..10) {
        Collections.shuffle(items)
        items.take(Random().nextInt(10)+5).forEach {
            val futurePurchase = realm.createObject(FuturePurchase::class)
            futurePurchase.creationDate = now().minusDays(i).toDate()
            futurePurchase.lastUpdate = futurePurchase.creationDate
            futurePurchase.item = it
            futurePurchase.amount = Random().nextInt(3) + 1f
        }
    }
}

private fun providePurchase(realm: Realm, name: String, category: String) {
    val currencies = SUPPORTED_CURRENCIES.toList()

    val shopsNum = realm.where(Shop::class.java).count().toInt()
    val shops = realm.where(Shop::class.java).findAll()

    val rand = Random().nextInt(1_000_000)
    val randShopNum = rand % shopsNum
    val randCurrencyNum = rand % currencies.size

    val shop = shops[randShopNum]
    val currency = currencies[randCurrencyNum]

    providePurchase(
            realm,
            shop,
            Random().nextInt(10).days().ago(),
            name,
            Db.createOrReturnCategory(realm, category),
            Random().nextFloat() * 500,
            currency
    )
}

private fun providePriceHistory(realm: Realm, shop: Shop, date: DateTime, value: Float, currency: Currency): PriceHistory {
    val price = Price()
    price.date = (date - 1.minutes().minutes).toDate()
    price.value = value
    price.currency = currency

    val priceHistory = realm.createObject(PriceHistory::class)
    priceHistory.shop = shop
    priceHistory.values = RealmList(realm.copyToRealmOrUpdate(price))

    return priceHistory
}

private fun providePurchase(realm: Realm, shop: Shop, date: DateTime, name: String, category: Category, price: Float, currency: Currency): Purchase {
    val purchase = realm.createObject(Purchase::class)
    purchase.amount = 1f + Random().nextInt(2)
    purchase.date = date.toDate()

    val item = realm.createObject(Item::class)
    item.name = name
    item.category = category
    item.priceHistory = providePriceHistory(realm, shop, date, price, currency)
    item.isFavorite = Random().nextBoolean()

    purchase.item = item

    return purchase
}

fun createDummyShops(ctx: Context, realm: Realm) {
    realm.executeTransaction {
        val silpo = it.createObject(Shop::class)
        silpo.name = "Сильпо"

        val atb = it.createObject(Shop::class)
        atb.name = "АТБ"
    }
}
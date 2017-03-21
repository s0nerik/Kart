package com.github.s0nerik.shoppingassistant

import android.content.Context
import com.github.debop.kodatimes.ago
import com.github.debop.kodatimes.days
import com.github.debop.kodatimes.minutes
import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.model.Currency
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
        createPurchases(it, "Food", "Картошка", "Мясо", "Молоко", "Рыба", "Помидоры")
        createPurchases(it, "Clothes", "Штаны", "Свитер", "Пальто", "Шарф", "Рубашка")
        createPurchases(it, "Peripherals", "Mouse", "Keyboard", "Headphones")
        createPurchases(it, "Software", "IntelliJ IDEA", "ReSharper")
        createPurchases(it, "Music", "Master Of Puppets", "Ungrateful")
        createPurchases(it, "Household", "Vacuum cleaner", "Teapot", "Cup")
        createPurchases(it, "Drinks", "Vodka", "Wine", "Tea")
        createPurchases(it, "Phones", "Samsung Galaxy S8", "Xiaomi Mi Mix", "HTC 11")
        createPurchases(it, "Sportswear", "Trousers", "Boots", "Helmet")
        createPurchases(it, "Presents", "Barbie")
    }
}

private fun createPurchases(realm: Realm, category: String, vararg names: String) {
    names.forEach { name ->
        providePurchase(realm, name, category)
    }
}

private fun providePurchase(realm: Realm, name: String, category: String) {
    val currenciesNum = realm.where(Currency::class.java).count().toInt()
    val currencies = realm.where(Currency::class.java).findAll()

    val shopsNum = realm.where(Shop::class.java).count().toInt()
    val shops = realm.where(Shop::class.java).findAll()

    val rand = Random().nextInt(1_000_000)
    val randShopNum = rand % shopsNum
    val randCurrencyNum = rand % currenciesNum

    val shop = shops[randShopNum]
    val currency = currencies[randCurrencyNum]

    providePurchase(
            realm,
            shop,
            Random().nextInt(10).days().ago(),
            name,
            category(realm, category),
            Random().nextFloat() * 500,
            currency
    )
}

private fun providePrice(realm: Realm, shop: Shop, date: DateTime, value: Float, currency: Currency): Price {
    val priceChange = realm.createObject(PriceChange::class)
    priceChange.date = (date - 1.minutes().minutes).toDate()
    priceChange.value = value
    priceChange.currency = currency

    val price = realm.createObject(Price::class)
    price.shop = shop
    price.valueChanges = RealmList(priceChange)

    return price
}

private fun providePurchase(realm: Realm, shop: Shop, date: DateTime, name: String, category: Category, price: Float, currency: Currency): Purchase {
    val purchase = realm.createObject(Purchase::class)
    purchase.amount = 1f + Random().nextInt(2)
    purchase.date = date.toDate()

    val item = realm.createObject(Item::class)
    item.name = name
    item.category = category
    item.price = providePrice(realm, shop, date, price, currency)

    purchase.item = item

    return purchase
}

private fun category(realm: Realm, name: String): Category {
    val category = realm.where(Category::class.java).equalTo("name", name).findFirst()

    if (category != null) {
        return category
    } else {
        return createCategory(realm, name, null)
    }
}

private fun createCategory(realm: Realm, name: String, iconUrl: String?): Category {
    val category = realm.createObject(Category::class)
    category.name = name
    if (iconUrl != null) category.iconUrl = iconUrl

    return category
}

fun createDummyCategories(ctx: Context, realm: Realm) {
    realm.executeTransaction {
        createCategory(it, "Food", R.drawable.product_cat_food.getDrawablePath(ctx))
        createCategory(it, "Clothes", R.drawable.product_cat_clothes.getDrawablePath(ctx))
    }
}

fun createDummyShops(ctx: Context, realm: Realm) {
    realm.executeTransaction {
        val silpo = it.createObject(Shop::class)
        silpo.name = "Сильпо"

        val atb = it.createObject(Shop::class)
        atb.name = "АТБ"
    }
}
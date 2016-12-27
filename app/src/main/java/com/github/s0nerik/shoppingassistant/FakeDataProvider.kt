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

fun createDummyPurchases(ctx: Context) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        realm.deleteAll()

        //region Currencies
        val usd = it.createObject(Currency::class.java, 0)
        usd.name = "USD"
        usd.sign = "$"

        val uah = it.createObject(Currency::class.java, 1)
        uah.name = "UAH"
        uah.sign = "₴"
        //endregion

        //region Categories
        val food = it.createObject(Category::class.java, 0)
        food.name = "Food"
        food.iconUrl = R.drawable.cat_food.getDrawableUri(ctx).toString()

        val clothes = it.createObject(Category::class.java, 1)
        clothes.name = "Clothes"
//            clothes.iconUrl = "https://api.icons8.com/download/c5c8b5ba35e008ea471e9a53c5fa74c03ef6e78c/iOS7/PNG/256/Very_Basic/search-256.png"
        clothes.iconUrl = R.drawable.cat_clothes.getDrawableUri(ctx).toString()
        //endregion

        //region Shops
        val silpo = it.createObject(Shop::class.java, 0)
        silpo.name = "Сильпо"

        val atb = it.createObject(Shop::class.java, 1)
        atb.name = "АТБ"
        //endregion

        val providePrice: (Long, Shop, DateTime, Float, Currency) -> Price = { id, shop, date, value, currency ->
            val priceChange = it.createObject(PriceChange::class.java, id)
            priceChange.date = (date - 1.minutes().minutes).toDate()
            priceChange.value = value
            priceChange.currency = currency

            val price = it.createObject(Price::class.java, id)
            price.shop = shop
            price.valueChanges = RealmList(priceChange)

            price
        }

        val providePurchase: (Long, Shop, DateTime, String, Category, Float, Currency) -> Purchase = { id, shop, date, name, category, price, currency ->
            val purchase = it.createObject(Purchase::class.java, id)
            purchase.amount = 1 + Random().nextInt(2)
            purchase.date = date.toDate()

            val item = it.createObject(Item::class.java, id)
            item.name = name
            item.category = category
            item.price = providePrice(id, shop, date, price, currency)

            purchase.item = item

            purchase
        }

        val foodsNames = arrayOf("Картошка", "Мясо", "Молоко", "Рыба", "Помидоры")
        val clothesNames = arrayOf("Штаны", "Свитер", "Пальто", "Шарф", "Рубашка")

        (0..9).forEach { i ->
            providePurchase(
                    i.toLong(),
                    if (i % 2 == 0) atb else silpo,
                    Random().nextInt(10).days().ago(),
                    if (i % 2 == 0) foodsNames[i / 2] else clothesNames[i / 2],
                    if (i % 2 == 0) food else clothes,
                    Random().nextFloat() * 500,
                    if (i % 2 == 0) usd else uah
            )
        }
    }
    realm.close()
}
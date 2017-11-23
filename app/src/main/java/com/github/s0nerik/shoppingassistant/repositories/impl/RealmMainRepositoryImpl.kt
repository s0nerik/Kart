package com.github.s0nerik.shoppingassistant.repositories.impl

import android.content.Context
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.vicpin.krealmextensions.saveManaged
import io.reactivex.Single
import io.realm.Realm
import io.realm.Sort
import org.json.JSONArray
import java.util.*


/**
 * Created by Alex Isaienko on 10/1/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class RealmMainRepositoryImpl : IMainRepository {
    private val ctx: Context
        get() = App.context

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    private fun createBasicCategories(realm: Realm) {
        realm.executeTransaction {
            val categories = JSONArray(ctx.resources.openRawResource(R.raw.categories).bufferedReader().use { it.readText() })
            for (i in 0..(categories.length() - 1)) {
                val category = categories.getJSONObject(i)
                val name = category.getString("name")
                val iconId = ctx.resources.getIdentifier(category.getString("icon"), "drawable", ctx.packageName)
                createOrReturnCategory(it, name, iconId.getDrawablePath())
            }
        }
    }

    private fun createOrReturnCategory(realm: Realm, name: String, iconUrl: String? = null): RealmCategory {
        val presentCategory = realm.where(RealmCategory::class.java).equalTo("name", name).findFirst()
        if (presentCategory != null) return presentCategory

        val category = realm.createObject(RealmCategory::class)
        category.name = name
        if (iconUrl != null) category.iconUrl = iconUrl

        return category
    }

    override fun init() {
        realm.use {
            it.executeTransaction(Realm::deleteAll)

            createBasicCategories(it)

            FakeDataProvider.createDummyShops(it)
            FakeDataProvider.createDummyPurchases(it)
        }
    }

    override fun getPurchases(page: Int, perPage: Int): Single<List<Purchase>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            realm.where(RealmPurchase::class.java).findAll().map { Purchase.from(it) }
        }
    }

    override fun getFuturePurchases(page: Int, perPage: Int): Single<List<FuturePurchase>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            realm.where(RealmFuturePurchase::class.java)
                    .findAllSorted("lastUpdate", Sort.DESCENDING)
                    .map { FuturePurchase.from(it) }
        }
    }

    override fun getRecentPurchases(fromDate: Date?, page: Int, perPage: Int): Single<List<Purchase>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            var query = realm.where(RealmPurchase::class.java)
            fromDate?.let { query = query.greaterThan("date", it) }
            query.findAllSorted("date", Sort.DESCENDING).map { Purchase.from(it) }
        }
    }

    override fun getItems(page: Int, perPage: Int): Single<List<Item>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            realm.where(RealmItem::class.java).findAll().map { Item.from(it) }
        }
    }

    override fun getFavoriteItems(page: Int, perPage: Int): Single<List<Item>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            realm.where(RealmItem::class.java)
                    .equalTo("isFavorite", true)
                    .findAll()
                    .map { Item.from(it) }
        }
    }

    override fun getFrequentItems(page: Int, perPage: Int): Single<List<Item>> {
        return Single.fromCallable {
            if (page >= 0) TODO("Implement paging")

            val purchases = realm.where(RealmPurchase::class.java).findAll()

            purchases.map { Item.from(it.item!!) }
                    .distinct()
                    .sortedByDescending { purchases.where().equalTo("item.id", it.id).count() }
//                    .slice(page*perPage..((page+1)*perPage - 1))
        }
    }

    override fun getShops(page: Int, perPage: Int): Single<List<Shop>> {
        return Single.fromCallable {
            realm.where(RealmShop::class.java).findAll().map { Shop.from(it) }
        }
    }

    override fun getCategories(page: Int, perPage: Int): Single<List<Category>> {
        return Single.fromCallable {
            realm.where(RealmCategory::class.java).findAll().map { Category.from(it) }
        }
    }

    override fun getMoneySpent(fromDate: Date): Single<Double> {
        return getRecentPurchases(fromDate).map { it.sumByDouble { it.fullPrice.toDouble() } }
    }

    override fun save(purchase: Purchase): Single<Purchase> {
        return Single.fromCallable {
            Purchase.from(RealmPurchase.from(purchase).saveManaged(realm))
        }
    }

    override fun delete(purchase: Purchase): Single<Unit> {
        return Single.fromCallable {
            realm.where(RealmPurchase::class.java).equalTo("id", purchase.id).findFirst()?.deleteFromRealm()
        }
    }

    override fun save(cart: Cart): Single<Cart> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
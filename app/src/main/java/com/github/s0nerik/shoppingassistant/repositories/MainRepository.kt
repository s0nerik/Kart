package com.github.s0nerik.shoppingassistant.repositories

import com.github.s0nerik.shoppingassistant.model.*
import com.github.s0nerik.shoppingassistant.repositories.impl.RealmMainRepositoryImpl
import io.reactivex.Single
import java.util.*


/**
 * Created by Alex Isaienko on 10/1/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface IMainRepository {
    fun init()

    fun getPurchases(page: Int = -1, perPage: Int = 10) : Single<List<Purchase>>
    fun getRecentPurchases(fromDate: Date? = null, page: Int = -1, perPage: Int = 10) : Single<List<Purchase>>

    fun getItems(page: Int = -1, perPage: Int = 10) : Single<List<Item>>
    fun getFavoriteItems(page: Int = -1, perPage: Int = 10) : Single<List<Item>>
    fun getFrequentItems(page: Int = -1, perPage: Int = 10) : Single<List<Item>>

    fun getShops(page: Int = -1, perPage: Int = 10) : Single<List<Shop>>
    fun getCategories(page: Int = -1, perPage: Int = 10) : Single<List<Category>>

    fun getMoneySpent(fromDate: Date = Date(0)): Single<Double>

    fun save(purchase: Purchase): Single<Purchase>
    fun delete(purchase: Purchase): Single<Unit>

    fun save(cart: Cart): Single<Cart>
}

object MainRepository : IMainRepository by RealmMainRepositoryImpl()
package com.github.s0nerik.shoppingassistant.repositories.stats

import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.reactivex.Single
import java.util.*


/**
 * Created by Alex Isaienko on 10/1/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
typealias PurchaseCategoryDistribution = Map<Category?, List<Purchase>>

interface IStatsRepository {
    fun getPurchaseCategoryDistribution(fromDate: Date = Date(0)): Single<PurchaseCategoryDistribution>
}

object StatsRepository : IStatsRepository by StatsRepositoryImpl()
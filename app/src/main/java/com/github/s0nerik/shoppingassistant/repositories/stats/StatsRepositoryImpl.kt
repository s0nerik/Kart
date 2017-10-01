package com.github.s0nerik.shoppingassistant.repositories.stats

import io.reactivex.Single
import java.util.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class StatsRepositoryImpl : IStatsRepository {
    override fun getPurchaseCategoryDistribution(fromDate: Date): Single<PurchaseCategoryDistribution> {
        return Single.just(emptyMap())
    }
}
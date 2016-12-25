package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Price(
        @PrimaryKey open var id: Long = 0,
        open var shop: Shop? = null,
        open var valueChanges: RealmList<PriceChange> = RealmList()
) : RealmObject() {
    // TODO: implement this
    fun getPriceForDate(date: Date): Float? {
        if (valueChanges.size > 0) {
            var price: Float? = null
            valueChanges.sortedBy { it.date }.forEach {
                if (date < it.date!!) {
                    return price
                }
                price = it.value
            }
            return price
        } else {
            return null
        }
    }
}
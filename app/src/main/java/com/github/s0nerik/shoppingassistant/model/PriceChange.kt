package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.randomUuidString
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class PriceChange(
        @PrimaryKey open var id: String = randomUuidString(),
        open var value: Float = 0f,
        open var currency: Currency? = null,
        open var date: Date? = null,
        open var quantityQualifierValue: String = PriceChange.QuantityQualifier.ITEM.name
) : RealmObject() {
    enum class QuantityQualifier { ITEM, KG }

    var quantityQualifier: QuantityQualifier
        get() = QuantityQualifier.valueOf(quantityQualifierValue)
        set(value) {
            quantityQualifierValue = value.name
        }
}
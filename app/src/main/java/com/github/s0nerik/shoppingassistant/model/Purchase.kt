package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
// TODO: add a way to distinguish different shops
// TODO: add a way for accounting price changes
open class Purchase(
        @PrimaryKey open var id: Long = 0,
        open var category: PurchaseCategory? = null,
        open var date: Date? = null,
        open var amount: Int = 0,
        open var itemPrice: Price? = null,
        open var currency: Currency
) : RealmObject()
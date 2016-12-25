package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class PriceChange(
        @PrimaryKey open var id: Long = 0,
        open var value: Float = 0f,
        open var date: Date? = null
) : RealmObject()
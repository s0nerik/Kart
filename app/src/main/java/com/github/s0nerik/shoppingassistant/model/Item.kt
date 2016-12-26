package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Item(
        @PrimaryKey open var id: Long = 0,
        open var name: String = "",
        open var category: Category? = null,
        open var price: Price? = null
) : RealmObject()
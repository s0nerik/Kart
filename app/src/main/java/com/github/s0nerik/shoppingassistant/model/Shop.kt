package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Shop(
        @PrimaryKey open var id: String = "",
        open var name: String = ""
) : RealmObject()
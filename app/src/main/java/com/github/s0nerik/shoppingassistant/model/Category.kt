package com.github.s0nerik.shoppingassistant.model


import com.github.s0nerik.shoppingassistant.Db
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Category(
        @PrimaryKey open var id: String = Db.randomUuidString(),
        open var name: String = "",
        open var iconUrl: String = ""
) : RealmObject()
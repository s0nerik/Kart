package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.randomUuidString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 2/24/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Cart(
        @PrimaryKey open var id: String = randomUuidString(),
        open var date: Date = Date(),
        open var purchases: RealmList<Purchase> = RealmList()
) : RealmObject()
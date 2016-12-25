package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Currency(
        open var id: Long = 0,
        open var sign: String = "",
        open var name: String = ""
) : RealmObject() {
    // TODO: add a way to convert between the currencies (probably async, using backend)
}
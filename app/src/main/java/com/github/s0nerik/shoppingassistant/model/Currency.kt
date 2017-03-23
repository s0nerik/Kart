package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Currency(
        @PrimaryKey open var code: String = "",
        open var sign: String = "",
        open var name: String = ""
) : RealmObject() {
    override fun toString() =  sign

    // TODO: add a way to convert between the currencies (probably async, using backend)
}
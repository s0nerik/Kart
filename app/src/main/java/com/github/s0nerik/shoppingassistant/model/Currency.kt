package com.github.s0nerik.shoppingassistant.model

import com.vicpin.krealmextensions.queryFirst
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class Currency(
        @PrimaryKey open var code: String = "",
        open var sign: String = "",
        open var name: String = "",
        open var isDefault: Boolean = false
) : RealmObject() {
    override fun toString() =  sign

    companion object {
        val default: Currency
            get() = Currency().queryFirst { it.equalTo("isDefault", true) } ?: localRealmCurrency

        private val localCurrency: java.util.Currency
            get() = java.util.Currency.getInstance(Locale.getDefault())

        private val localRealmCurrency: Currency
            get() = with(localCurrency) { Currency(currencyCode, symbol, displayName, true) }
    }
    // TODO: add a way to convert between the currencies (probably async, using backend)
}


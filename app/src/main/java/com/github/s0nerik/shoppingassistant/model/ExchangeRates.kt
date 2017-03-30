package com.github.s0nerik.shoppingassistant.model

import io.realm.RealmObject
import java.util.*

/**
 * Created by Alex on 3/30/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

open class ExchangeRates(var date: Date = Date(), var source: Currency = Currency.default, var rates: List<ExchangeRate>) : RealmObject()
open class ExchangeRate(var currency: String, var value: Float) : RealmObject()

data class RemoteExchangeRates(val timestamp: Long, val source: String, val rates: List<ExchangeRate>)
data class RemoteExchangeRate(val currency: String, val value: Float)
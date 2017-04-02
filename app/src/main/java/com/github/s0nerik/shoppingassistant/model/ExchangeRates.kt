package com.github.s0nerik.shoppingassistant.model

import com.github.s0nerik.shoppingassistant.MainPrefs
import com.vicpin.krealmextensions.save
import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

/**
 * Created by Alex on 3/30/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

open class ExchangeRates(
        open var date: Date = Date(),
        open var sourceCurrencyCode: String = MainPrefs.defaultCurrencyCode,
        open var rates: RealmList<ExchangeRate> = RealmList()
) : RealmObject()

open class ExchangeRate(
        open var currencyCode: String = MainPrefs.defaultCurrencyCode,
        open var value: Float = 0f
) : RealmObject()

data class RemoteExchangeRates(val timestamp: Long, val source: String, val rates: List<RemoteExchangeRate>) {
    fun saveToDatabase() {
        val exchangeRates = ExchangeRates(Date(timestamp * 1000), source)
        exchangeRates.rates.addAll(rates.map { ExchangeRate(it.currency, it.value) })

        exchangeRates.save()
    }
}
data class RemoteExchangeRate(val currency: String, val value: Float)
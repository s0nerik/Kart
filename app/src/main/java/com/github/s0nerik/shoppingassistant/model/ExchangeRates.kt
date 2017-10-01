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
data class ExchangeRates(
        val date: Date,
        val sourceCurrencyCode: String,
        val rates: List<ExchangeRate>
) {
    companion object {
        fun from(e: RealmExchangeRates): ExchangeRates {
            return ExchangeRates(e.date, e.sourceCurrencyCode, e.rates.map { ExchangeRate.from(it) })
        }
    }
}

data class ExchangeRate(
        val currencyCode: String,
        val value: Float
) {
    companion object {
        fun from(e: RealmExchangeRate): ExchangeRate {
            return ExchangeRate(e.currencyCode, e.value)
        }
    }
}

open class RealmExchangeRates(
        open var date: Date = Date(),
        open var sourceCurrencyCode: String = MainPrefs.defaultCurrencyCode,
        open var rates: RealmList<RealmExchangeRate> = RealmList()
) : RealmObject()

open class RealmExchangeRate(
        open var currencyCode: String = MainPrefs.defaultCurrencyCode,
        open var value: Float = 0f
) : RealmObject()

data class RemoteExchangeRates(val timestamp: Long, val source: String, val rates: List<RemoteExchangeRate>) {
    fun saveToDatabase() {
        val exchangeRates = RealmExchangeRates(Date(timestamp * 1000), source)
        exchangeRates.rates.addAll(rates.map { RealmExchangeRate(it.currency, it.value) })

        exchangeRates.save()
    }
}
data class RemoteExchangeRate(val currency: String, val value: Float)
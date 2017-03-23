package com.github.s0nerik.shoppingassistant.adapter_items

import com.github.debop.kodatimes.toDateTime
import com.github.s0nerik.shoppingassistant.model.Currency
import java.util.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
typealias MoneySpent = Pair<Float, Currency>

data class HistoryHeader(val date: Date, val spentMoney: MoneySpent) {
    // TODO: localization
    val readableDate: String
        get() = date.toDateTime().toString("MMMM d")
    val readableSpentMoney: String
        get() = "${spentMoney.second} ${"%.2f".format(spentMoney.first)}"
}
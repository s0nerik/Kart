package com.github.s0nerik.shoppingassistant

import android.content.SharedPreferences
import android.support.annotation.StringRes
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.github.s0nerik.shoppingassistant.R.string.*
import com.github.s0nerik.shoppingassistant.ext.getString
import com.github.s0nerik.shoppingassistant.model.Currency

/**
 * Created by Alex on 3/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

enum class DashboardDataPeriod(@StringRes val stringId: Int) {
    TODAY(stats_period_today),
    YESTERDAY(stats_period_yesterday),
    LAST_WEEK(stats_period_last_week),
    LAST_MONTH(stats_period_last_month),
    SIX_MONTHS(stats_period_six_months),
    LAST_YEAR(stats_period_last_year);

    override fun toString(): String {
        return getString(stringId)
    }
}

enum class ExpensesLimitPeriod(@StringRes val stringId: Int, @StringRes val formatStringId: Int) {
    DAY(expenses_limit_period_day, expenses_limit_period_day_format),
    WEEK(expenses_limit_period_week, expenses_limit_period_week_format),
    MONTH(expenses_limit_period_month, expenses_limit_period_month_format);

    fun format(amount: Float): String {
        return "${Currency.default.sign} ${getString(formatStringId, amount)}"
    }

    override fun toString(): String {
        return getString(stringId)
    }
}

object DashboardPrefs : KotprefModel() {
    var dataPeriod by enumValuePref(DashboardDataPeriod.LAST_WEEK)
}

object MainPrefs : KotprefModel() {
    var expensesLimitPeriod by enumValuePref(ExpensesLimitPeriod.MONTH)
    var expensesLimit by floatPref(0F)

    val formattedExpensesLimit: String
        get() {
            if (expensesLimit > 0F) {
                return expensesLimitPeriod.format(expensesLimit)
            } else {
                return getString(R.string.expenses_limit_unknown)
            }
        }

    val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(kotprefName, kotprefMode)
}

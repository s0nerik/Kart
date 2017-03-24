package com.github.s0nerik.shoppingassistant

import com.chibatching.kotpref.KotprefModel

/**
 * Created by Alex on 3/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

object MainPrefs : KotprefModel() {
    var purchaseLimitDays by intPref(30)
    var purchaseLimit by floatPref(0F)
}
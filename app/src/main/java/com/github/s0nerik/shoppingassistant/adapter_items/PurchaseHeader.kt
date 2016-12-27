package com.github.s0nerik.shoppingassistant.adapter_items

import khronos.toString
import java.util.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class PurchaseHeader(val date: Date) {
    // TODO: localization
    val readableDate: String
        get() = date.toString("MMMM d")
}
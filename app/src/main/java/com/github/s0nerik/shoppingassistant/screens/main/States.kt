package com.github.s0nerik.shoppingassistant.screens.main

import android.support.annotation.IdRes
import com.github.s0nerik.shoppingassistant.R


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
enum class State(@IdRes val menuId: Int) {
    DASHBOARD(R.id.dashboard),
    HISTORY(R.id.history),
    PURCHASE_LIST(R.id.lists),
    CART(R.id.cart)
}
package com.github.s0nerik.shoppingassistant.screens.main.cart

import com.github.s0nerik.shoppingassistant.model.Item
import io.reactivex.Maybe

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface CartVmInteractor {
    fun createNewItem(): Maybe<Item>
}
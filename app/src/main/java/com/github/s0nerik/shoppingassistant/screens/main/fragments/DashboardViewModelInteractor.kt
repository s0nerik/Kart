package com.github.s0nerik.shoppingassistant.screens.main.fragments

import com.github.s0nerik.shoppingassistant.model.Item
import io.reactivex.Maybe


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface DashboardViewModelInteractor {
    fun createNewItem(): Maybe<Item>
}
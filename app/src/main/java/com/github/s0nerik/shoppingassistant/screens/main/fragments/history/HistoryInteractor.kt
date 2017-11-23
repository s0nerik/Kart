package com.github.s0nerik.shoppingassistant.screens.main.fragments.history

import com.github.s0nerik.shoppingassistant.model.Purchase

/**
 * Created by Alex on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface HistoryInteractor {
    fun onItemSelected(item: Purchase)
}
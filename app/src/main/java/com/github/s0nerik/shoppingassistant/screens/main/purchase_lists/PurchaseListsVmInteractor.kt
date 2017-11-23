package com.github.s0nerik.shoppingassistant.screens.main.purchase_lists

import com.github.s0nerik.shoppingassistant.model.Item
import io.reactivex.Maybe

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface PurchaseListsVmInteractor {
    fun provideNewItem(): Maybe<Item>
}
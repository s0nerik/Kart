package com.github.s0nerik.shoppingassistant.screens.product

import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.model.Price
import com.github.s0nerik.shoppingassistant.model.Shop
import io.reactivex.Maybe


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface CreateProductViewModelInteractor {
    fun selectPrice(): Maybe<Price>
    fun selectCategory(): Maybe<Category>
    fun selectShop(): Maybe<Shop>
    fun finishWithResult(item: Item?)
}
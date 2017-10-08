package com.github.s0nerik.shoppingassistant.screens.product.select_shop

import com.github.s0nerik.shoppingassistant.model.Shop


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface SelectShopViewModelInteractor {
    fun finishWithResult(shop: Shop?)
}
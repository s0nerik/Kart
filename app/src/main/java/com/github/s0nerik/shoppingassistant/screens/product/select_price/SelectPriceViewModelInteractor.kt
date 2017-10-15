package com.github.s0nerik.shoppingassistant.screens.product.select_price

import com.github.s0nerik.shoppingassistant.model.Price
import io.reactivex.Maybe
import java.util.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface SelectPriceViewModelInteractor {
    fun selectCurrency(current: Currency): Maybe<Currency>
    fun finishWithResult(price: Price?)
}
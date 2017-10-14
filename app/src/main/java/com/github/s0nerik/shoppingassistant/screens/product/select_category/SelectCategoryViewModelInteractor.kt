package com.github.s0nerik.shoppingassistant.screens.product.select_category

import com.github.s0nerik.shoppingassistant.model.Category


/**
 * Created by Alex Isaienko on 10/14/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface SelectCategoryViewModelInteractor {
    fun finishWithResult(category: Category?)
}
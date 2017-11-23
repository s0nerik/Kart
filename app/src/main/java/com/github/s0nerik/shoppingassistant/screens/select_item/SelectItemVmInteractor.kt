package com.github.s0nerik.shoppingassistant.screens.select_item

import com.github.s0nerik.shoppingassistant.model.Item
import io.reactivex.Maybe

/**
 * Created by Alex Isaienko on 11/22/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface SelectItemVmInteractor {
    fun provideVoiceRecognitionResult(): Maybe<String>
    fun createItem(name: String = ""): Maybe<Item>
    fun onItemSelected(item: Item)
}
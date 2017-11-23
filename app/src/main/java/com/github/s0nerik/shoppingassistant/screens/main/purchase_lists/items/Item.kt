package com.github.s0nerik.shoppingassistant.screens.main.purchase_lists.items

import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.s0nerik.shoppingassistant.model.FuturePurchase
import com.github.s0nerik.shoppingassistant.model.Item
import kotlinx.android.parcel.Parcelize

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Item(
    val futurePurchase: FuturePurchase
) : Parcelable {
    val purchaseItem: Item?
        get() = futurePurchase.item

    fun confirm() {
        TODO()
    }

    fun remove() {
        TODO()
    }
}
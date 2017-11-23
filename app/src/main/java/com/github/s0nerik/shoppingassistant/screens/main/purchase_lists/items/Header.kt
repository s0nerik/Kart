package com.github.s0nerik.shoppingassistant.screens.main.purchase_lists.items

import android.annotation.SuppressLint
import android.os.Parcelable
import com.github.debop.kodatimes.toDateTime
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Header(
    val date: Date
) : Parcelable {
    val readableDate: String
        get() = date.toDateTime().withTimeAtStartOfDay().toString("MMMM d")
}
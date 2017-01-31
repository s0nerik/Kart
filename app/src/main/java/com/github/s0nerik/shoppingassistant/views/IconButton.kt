package com.github.s0nerik.shoppingassistant.views

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.getColor
import com.github.s0nerik.shoppingassistant.getDimenPixelOffset

/**
 * Created by Alex on 1/25/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun getMargin(b: Boolean?): Int {
    if (b != null && b) {
        return b.getDimenPixelOffset(R.dimen.btn_icon_negative_margin)
    } else {
        return Any().getDimenPixelOffset(R.dimen.btn_icon_default_margin)
    }
}

fun getBgColor(bgColor: Int?, selected: Boolean?): Int {
    if (bgColor != null)
        return bgColor
    else
        if (selected == true)
            return Any().getColor(R.color.material_color_grey_800)
        else
            return Any().getColor(R.color.colorAccent)
}
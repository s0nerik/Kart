package com.github.s0nerik.shoppingassistant.views

import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.ext.getColor
import com.github.s0nerik.shoppingassistant.ext.getDimenPixelOffset

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

fun getBgColor(bgColor: Int?, selected: Boolean?, selectedColor: Int?, unselectedColor: Int?): Int {
    if (bgColor != null)
        return bgColor
    else
        if (selected == true)
            if (selectedColor == null)
                return Any().getColor(R.color.material_color_blue_grey_800)
            else
                return selectedColor
        else
            if (unselectedColor == null)
                return Any().getColor(R.color.colorAccent)
            else
                return unselectedColor
}
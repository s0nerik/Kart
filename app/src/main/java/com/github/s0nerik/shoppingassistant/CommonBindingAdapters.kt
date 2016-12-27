package com.github.s0nerik.shoppingassistant

import android.databinding.BindingAdapter
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.widget.TextView

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

@BindingAdapter("font")
fun setFont(textView: TextView, fontName: String) {
    textView.typeface = Typeface.createFromAsset(textView.context.assets, "fonts/$fontName.ttf")
}

@BindingAdapter(value = *arrayOf("menu", "menuTint"), requireAll = false)
fun setMenu(toolbar: Toolbar, @MenuRes menuId: Int, @ColorRes tintColorId: Int?) {
    toolbar.inflateMenu(menuId)
    if (tintColorId != null) {
        val menu = toolbar.menu
        for (i in 0..menu.size() - 1) {
            val item = menu.getItem(i)
            val icon = item.icon
            if (icon != null) {
                icon.setColorFilter(ContextCompat.getColor(toolbar.context, tintColorId), PorterDuff.Mode.SRC_IN)
                item.icon = icon
            }
        }
    }
}
package com.github.s0nerik.shoppingassistant

import android.content.Context
import android.content.res.Configuration
import android.databinding.BindingAdapter
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.*
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

@BindingAdapter("applyNavBarMargin")
fun applyNavBarMargin(view: View, apply: Boolean) {
    if (!apply) return

    val navBarHeight = getNavBarHeight(view.context)
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        bottomMargin += navBarHeight
    }
}

@BindingAdapter("applyNavBarPadding")
fun applyNavBarPadding(view: View, apply: Boolean) {
    if (!apply) return

    val navBarHeight = getNavBarHeight(view.context)
    view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom + navBarHeight)
}

@BindingAdapter("applyStatusBarMargin")
fun applyStatusBarMargin(view: View, apply: Boolean) {
    if (!apply) return

    val statusBarHeight = getStatusBarHeight(view.context)
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        topMargin += statusBarHeight
    }
}

@BindingAdapter("applyStatusBarPadding")
fun applyStatusBarPadding(view: View, apply: Boolean) {
    if (!apply) return

    val statusBarHeight = getStatusBarHeight(view.context)
    view.setPadding(view.paddingLeft, view.paddingTop + statusBarHeight, view.paddingRight, view.paddingBottom)
}

private fun getStatusBarHeight(c: Context): Int {
    val resourceId = c.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return c.resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

private fun getNavBarHeight(c: Context): Int {
    val hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    if (!hasMenuKey && !hasBackKey) {
        //The device has a navigation bar
        val resources = c.resources

        val orientation = resources.configuration.orientation
        val resourceId: Int
        if (isTablet(c)) {
            resourceId = resources.getIdentifier(if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape", "dimen", "android")
        } else {
            resourceId = resources.getIdentifier(if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_width", "dimen", "android")
        }

        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
    }
    return 0
}

private fun isTablet(c: Context): Boolean {
    return c.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}
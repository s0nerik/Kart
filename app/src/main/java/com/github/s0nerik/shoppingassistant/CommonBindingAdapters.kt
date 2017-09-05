package com.github.s0nerik.shoppingassistant

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TextView
import com.github.s0nerik.shoppingassistant.ext.safe
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.lang.ref.WeakReference
import java.util.*


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
    if (menuId != 0) toolbar.inflateMenu(menuId)
    if (tintColorId != null) {
        toolbar.navigationIcon?.apply { setColorFilter(ContextCompat.getColor(toolbar.context, tintColorId), PorterDuff.Mode.SRC_IN) }

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

val animatedBackgrounds = WeakHashMap<CardView, WeakReference<ValueAnimator>>()

@BindingAdapter("animateBackgroundColor")
fun setAnimateBackgroundColor(card: CardView, animate: Boolean) {
    val evaluator = ArgbEvaluator()
    val animator = ValueAnimator()
    animator.setIntValues(card.cardBackgroundColor.defaultColor, Color.parseColor("#22000000"))
    animator.setEvaluator(evaluator)
    animator.duration = 250
    animator.repeatCount = ValueAnimator.INFINITE
    animator.repeatMode = ValueAnimator.REVERSE
    animator.addUpdateListener { animation ->
        card.backgroundColor = animation.animatedValue as Int
    }
    animator.start()

    if (animate) {
        animatedBackgrounds[card]?.safe {

        }
    }
    card.cardBackgroundColor.defaultColor
}

@BindingAdapter("applyNavBarMargin")
fun applyNavBarMargin(view: View, apply: Boolean) {
    if (!apply) return

    val navBarHeight = getNavBarHeight(view.context)
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        bottomMargin += navBarHeight
    }
}

// TODO: Add windowSystemUiVisibility listener to handle System UI changes
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

@BindingAdapter("android:layout_marginTop")
fun setLayoutMarginTop(view: View, margin: Int) {
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(it.leftMargin, view.dip(margin), it.rightMargin, it.bottomMargin)
        view.layoutParams = it
    }
}

@BindingAdapter("android:layout_marginBottom")
fun setLayoutMarginBottom(view: View, margin: Int) {
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(it.leftMargin, it.topMargin, it.rightMargin, view.dip(margin))
        view.layoutParams = it
    }
}

@BindingAdapter("android:layout_marginLeft")
fun setLayoutMarginLeft(view: View, margin: Int) {
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(view.dip(margin), it.topMargin, it.rightMargin, it.bottomMargin)
        view.layoutParams = it
    }
}

@BindingAdapter("android:layout_marginRight")
fun setLayoutMarginRight(view: View, margin: Int) {
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(it.leftMargin, it.topMargin, view.dip(margin), it.bottomMargin)
        view.layoutParams = it
    }
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

    val hasNavBarId = c.resources.getIdentifier("config_showNavigationBar", "bool", "android")
    val hasNavBar = hasNavBarId > 0 && c.resources.getBoolean(hasNavBarId)

    if (!hasMenuKey && !hasBackKey || isEmulator() || hasNavBar) {
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
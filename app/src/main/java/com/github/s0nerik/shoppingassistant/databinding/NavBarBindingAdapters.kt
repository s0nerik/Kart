package com.github.s0nerik.shoppingassistant.databinding

import android.app.Activity
import android.databinding.BindingAdapter
import android.view.View
import android.view.WindowManager

/**
 * Created by Alex Isaienko on 11/27/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

@BindingAdapter("enableTranslucentNavBar")
fun paintNavBarBg(view: View, enableTranslucentNavBar: Boolean) {
    (view.context as? Activity)?.let { activity ->
        if (enableTranslucentNavBar) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }
}

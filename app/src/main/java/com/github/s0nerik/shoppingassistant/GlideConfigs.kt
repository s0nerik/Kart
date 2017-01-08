package com.github.s0nerik.shoppingassistant

import android.graphics.Color
import com.github.s0nerik.glide_bindingadapter.GlideBindingConfig
import jp.wasabeef.glide.transformations.ColorFilterTransformation

/**
 * Created by Alex on 1/1/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

// TODO: figure out what to do with local vector drawables not being loaded
fun configureGlide() {
    GlideBindingConfig.registerProvider("purchase_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.alert_circle_outline)
    })

    GlideBindingConfig.registerProvider("category_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.tag)
    })

    GlideBindingConfig.registerProvider("shop_category_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.store)
    })

    GlideBindingConfig.registerProvider("product_category_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.tag)
    })

    GlideBindingConfig.registerProvider("product_price_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.cash_multiple)
    })

    GlideBindingConfig.registerProvider("add_product_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.selection)
    })
}
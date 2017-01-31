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
                .error(R.drawable.tag)
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

    GlideBindingConfig.registerProvider("product_name_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.pencil)
    })

    GlideBindingConfig.registerProvider("create_purchase_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.ic_done_black_24dp)
    })

    GlideBindingConfig.registerProvider("cancel_purchase_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.ic_clear_black_24dp)
    })

    GlideBindingConfig.registerProvider("scan_barcode_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.barcode_scan)
    })

    GlideBindingConfig.registerProvider("product_favorite_icon", { iv, request ->
        request.bitmapTransform(ColorFilterTransformation(iv.context, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                .error(R.drawable.fav_no)
    })
}
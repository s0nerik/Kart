package com.github.s0nerik.shoppingassistant.ext

import com.github.s0nerik.shoppingassistant.App
import com.github.s0nerik.shoppingassistant.R
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration

/**
 * Created by Alex on 5/9/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

object RecyclerDivider {
    val horizontal: HorizontalDividerItemDecoration by lazy {
        HorizontalDividerItemDecoration.Builder(App.context)
                .drawable(R.drawable.divider_horizontal_gradient_left)
                .sizeResId(R.dimen.material_divider_height)
//                .margin(App.context.dip(48), 0)
                .build()
    }

    val vertical: VerticalDividerItemDecoration by lazy {
        VerticalDividerItemDecoration.Builder(App.context)
                .drawable(R.drawable.divider_vertical_gradient)
                .sizeResId(R.dimen.material_divider_height)
                .build()
    }
}
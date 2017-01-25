//package com.github.s0nerik.shoppingassistant.views
//
//import android.content.Context
//import android.graphics.Color
//import android.support.v4.content.ContextCompat
//import android.support.v7.widget.CardView
//import android.util.AttributeSet
//import com.github.s0nerik.shoppingassistant.R
//import kotlinx.android.synthetic.main.view_btn_icon.view.*
//import org.jetbrains.anko.dip
//
//
///**
// * Created by Alex on 1/24/2017.
// * GitHub: https://github.com/s0nerik
// * LinkedIn: https://linkedin.com/in/sonerik
// */
//class IconButtonView @JvmOverloads constructor(
//        context: Context,
//        attrs: AttributeSet? = null,
//        defStyleAttr: Int = 0
//) : CardView(context, attrs, defStyleAttr) {
//    private val position: String?
//    private val negativeMargin: Int
//    private val bgColor: Int
//    private val iconColor: Int
//
//    init {
//        inflate(context, R.layout.view_btn_icon, this)
//
//        val a = context.obtainStyledAttributes(attrs, R.styleable.IconButtonView)
//        try {
//            position = a.getString(R.styleable.IconButtonView_ibv_position)
//            negativeMargin = a.getDimensionPixelOffset(R.styleable.IconButtonView_ibv_negativeMargin, dip(2))
//            bgColor = a.getColor(R.styleable.IconButtonView_ibv_bgColor, ContextCompat.getColor(context, R.attr.colorAccent))
//            iconColor = a.getColor(R.styleable.IconButtonView_ibv_iconColor, Color.WHITE)
//        } finally {
//            a.recycle()
//        }
//
//        icon.setColorFilter(iconColor)
//        btn.setCardBackgroundColor(bgColor)
//    }
//
//    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        (layoutParams as? MarginLayoutParams)?.apply {
//            when (position) {
//                "left" -> rightMargin -= negativeMargin
//                "right" -> leftMargin -= negativeMargin
//                "top" -> bottomMargin -= negativeMargin
//                "bottom" -> topMargin -= negativeMargin
//            }
//            layoutParams = this
//        }
//    }
//}
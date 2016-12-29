package com.github.s0nerik.shoppingassistant.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity

abstract class BaseActivity : RxAppCompatActivity() {
    protected open val layoutId: Int?
        get() = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId != null) setContentView(layoutId!!)
    }
}

abstract class BaseBoundActivity<out T : ViewDataBinding>(
        protected val layoutId: Int
) : RxAppCompatActivity() {
    private lateinit var innerBinding: T
    protected val binding: T by lazy { innerBinding }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        innerBinding = DataBindingUtil.setContentView(this, layoutId)
    }
}
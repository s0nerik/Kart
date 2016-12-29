package com.github.s0nerik.shoppingassistant.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import io.realm.Realm

abstract class BaseActivity : RxAppCompatActivity() {
    protected open val layoutId: Int?
        get() = null

    protected val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId != null) setContentView(layoutId!!)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}

abstract class BaseBoundActivity<out T : ViewDataBinding>(
        protected val layoutId: Int
) : RxAppCompatActivity() {
    private lateinit var innerBinding: T
    protected val binding: T by lazy { innerBinding }

    protected val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        innerBinding = DataBindingUtil.setContentView(this, layoutId)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
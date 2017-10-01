package com.github.s0nerik.shoppingassistant.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import com.github.s0nerik.shoppingassistant.BR
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlin.reflect.KClass

abstract class BaseActivity(
        private val layoutId: Int? = null
) : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutId?.let { setContentView(it) }
    }

    fun Fragment.replaceAndCommit(@IdRes containerId: Int, addToBackStack: Boolean = false, backStackTag: String? = null) {
        var transaction = supportFragmentManager.beginTransaction()
                .replace(containerId, this)

        if (addToBackStack)
            transaction = transaction.addToBackStack(null)

        if (addToBackStack)
            transaction.commit()
        else
            transaction.commitNow()
    }
}

abstract class BaseBoundActivity<out TBinding : ViewDataBinding>(
        private val layoutId: Int,
        private val disableTransitions: Boolean = false
) : BaseActivity(layoutId) {
    private lateinit var innerBinding: TBinding
    protected val binding: TBinding by lazy { innerBinding }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (disableTransitions) overridePendingTransition(0, 0)
        innerBinding = DataBindingUtil.setContentView(this, layoutId)
    }
}

abstract class BaseBoundVmActivity<out TBinding : ViewDataBinding, out TViewModel : ViewModel>(
        layoutId: Int,
        private val vmClass: KClass<TViewModel>,
        private val autoBindVm: Boolean = true,
        disableTransitions: Boolean = false
) : BaseBoundActivity<TBinding>(layoutId, disableTransitions) {
    protected val vm: TViewModel by lazy { ViewModelProviders.of(this).get(vmClass.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (autoBindVm) binding.setVariable(BR.vm, vm)
    }
}
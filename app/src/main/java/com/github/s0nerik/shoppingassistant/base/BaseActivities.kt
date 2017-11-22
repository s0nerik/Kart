package com.github.s0nerik.shoppingassistant.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import com.github.s0nerik.shoppingassistant.BR
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Maybe
import io.reactivex.subjects.PublishSubject
import kotlin.reflect.KClass

/**
 * Created by Alex Isaienko on 10/16/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
abstract class BaseActivity(
        private val layoutId: Int? = null
) : RxAppCompatActivity() {
    private val activityResults = PublishSubject.create<ActivityResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutId?.let { setContentLayout(it) }
    }

    protected open fun setContentLayout(@LayoutRes layoutId: Int) {
        setContentView(layoutId)
    }

    fun Fragment.replaceAndCommit(@IdRes containerId: Int, addToBackStack: Boolean = false, tag: String? = null) {
        var transaction = supportFragmentManager.beginTransaction()
                .replace(containerId, this, tag)

        if (addToBackStack)
            transaction = transaction.addToBackStack(tag)

        if (addToBackStack)
            transaction.commit()
        else
            transaction.commitNow()
    }

    final override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    @SuppressLint("RestrictedApi")
    final override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
    }

    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResults.onNext(ActivityResult(requestCode, resultCode, data))
    }

    protected fun awaitActivityResult(requestCode: Int): Maybe<ActivityResult> = activityResults.filter { it.requestCode == requestCode }.firstElement()

    fun startForResult(intent: Intent, requestCode: Int): Maybe<ActivityResult> = Maybe.defer {
        this.startActivityForResult(intent, requestCode)
        awaitActivityResult(requestCode)
    }
}

abstract class BaseBoundActivity<out TBinding : ViewDataBinding>(
        layoutId: Int,
        private val disableTransitions: Boolean = false
) : BaseActivity(layoutId) {
    private lateinit var innerBinding: TBinding
    protected val binding: TBinding by lazy { innerBinding }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (disableTransitions) overridePendingTransition(0, 0)
    }

    override fun setContentLayout(layoutId: Int) {
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
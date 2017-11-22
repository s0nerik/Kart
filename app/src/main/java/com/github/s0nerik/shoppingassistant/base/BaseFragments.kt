package com.github.s0nerik.shoppingassistant.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.s0nerik.shoppingassistant.BR
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.Maybe
import io.reactivex.subjects.PublishSubject
import kotlin.reflect.KClass

/**
 * Created by Alex Isaienko on 10/16/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
data class ActivityResult(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
)

abstract class BaseFragment(
        protected val layoutId: Int
) : RxFragment() {
    private val activityResults = PublishSubject.create<ActivityResult>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutId, container, false)
    }

    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResults.onNext(ActivityResult(requestCode, resultCode, data))
    }

    protected fun awaitActivityResult(requestCode: Int): Maybe<ActivityResult> = activityResults.filter { it.requestCode == requestCode }.firstElement()

    protected inline fun <reified TViewModel: ViewModel> getActivityViewModel(): TViewModel {
        return ViewModelProviders.of(activity!!).get(TViewModel::class.java)
    }
}

abstract class BaseBoundFragment<out TBinding : ViewDataBinding>(layoutId: Int) : BaseFragment(layoutId) {
    private lateinit var innerBinding: TBinding
    protected val binding
        get() = innerBinding

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)
        innerBinding = DataBindingUtil.bind(view)
        return view
    }
}

abstract class BaseBoundVmFragment<out TBinding : ViewDataBinding, out TViewModel : ViewModel>(
        layoutId: Int,
        private val vmClass: KClass<TViewModel>,
        private val autoBindVm: Boolean = true
) : BaseBoundFragment<TBinding>(layoutId) {
    protected val vm: TViewModel by lazy { ViewModelProviders.of(this).get(vmClass.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (autoBindVm) binding.setVariable(BR.vm, vm)
    }
}
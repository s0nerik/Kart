package com.github.s0nerik.shoppingassistant.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.s0nerik.shoppingassistant.BR
import io.realm.Realm

/**
 * Created by Alex on 3/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class BaseBottomSheet<out T, V : ViewDataBinding>(val vm: T, @LayoutRes val layout: Int) : BottomSheetDialogFragment() {
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    lateinit var binding: V

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<V>(inflater, layout, container, false)
        binding.setVariable(BR.vm, vm)
        return binding.root
    }
}
package com.github.s0nerik.shoppingassistant.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment(
        protected val layoutId: Int
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)
        return view
    }
}

abstract class BaseBoundFragment<out T : ViewDataBinding>(
        protected val layoutId: Int
) : Fragment() {
    private lateinit var innerBinding: T
    protected val binding: T by lazy { innerBinding }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)
        innerBinding = DataBindingUtil.bind(view)
        return view
    }
}
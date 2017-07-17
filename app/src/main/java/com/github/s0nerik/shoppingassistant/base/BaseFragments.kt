package com.github.s0nerik.shoppingassistant.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxFragment
import io.realm.Realm

abstract class BaseFragment(
        protected val layoutId: Int
) : RxFragment() {
    private lateinit var innerRealm: Realm
    protected val realm
        get() = innerRealm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        innerRealm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        innerRealm.close()
        super.onDestroy()
    }
}

abstract class BaseBoundFragment<out T : ViewDataBinding>(
        protected val layoutId: Int
) : RxFragment() {
    private lateinit var innerBinding: T
    protected val binding
        get() = innerBinding

    private lateinit var innerRealm: Realm
    val realm
        get() = innerRealm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)
        innerBinding = DataBindingUtil.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        innerRealm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        innerRealm.close()
        super.onDestroy()
    }
}
package com.github.s0nerik.shoppingassistant.base

import android.support.v4.app.FragmentManager
import io.reactivex.Maybe


/**
 * Created by Alex Isaienko on 10/16/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
interface FragmentResultProvider<TResult> {
    fun startForResult(fragmentManager: FragmentManager): Maybe<TResult>
}
package com.github.s0nerik.shoppingassistant.ext

import android.view.View
import co.metalab.asyncawait.AsyncController
import co.metalab.asyncawait.await
import com.jakewharton.rxbinding.view.preDraws
import rx.functions.Func0

/**
 * Created by Alex on 2/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun View.appearScaleIn(duration: Long = 200) {
    scaleX = 0f
    scaleY = 0f
    animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
}

fun View.disappearScaleOut(duration: Long = 200) {
    animate().scaleX(0f).scaleY(0f).setDuration(duration).start()
}

suspend fun AsyncController.awaitPreDraw(v: View) = this.await(v.preDraws(Func0 { true }).onMainThread())
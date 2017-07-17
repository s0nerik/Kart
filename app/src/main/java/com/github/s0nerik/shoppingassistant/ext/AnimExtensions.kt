package com.github.s0nerik.shoppingassistant.ext

import android.view.View
import com.jakewharton.rxbinding2.view.preDraws
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.rx2.awaitFirst
import java.util.concurrent.Callable

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

suspend fun Deferred<Boolean>.awaitPreDraw(v: View) = v.preDraws(Callable { true }).onMainThread().awaitFirst()
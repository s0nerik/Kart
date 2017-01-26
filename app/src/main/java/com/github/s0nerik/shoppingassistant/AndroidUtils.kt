package com.github.s0nerik.shoppingassistant

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.View
import org.jetbrains.anko.inputMethodManager


/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

/**
 * get uri to drawable or any other resource type if u wish
 * @param context - context
 * @return - uri
 */
fun Int.getDrawableUri(context: Context): Uri {
    val imageUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.resources.getResourcePackageName(this)}/${context.resources.getResourceTypeName(this)}/${context.resources.getResourceEntryName(this)}"
    )
    return imageUri
}

fun Context.showKeyboard(view: View) {
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.hideKeyboard(view: View) {
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun isEmulator(): Boolean {
    return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || "google_sdk" == Build.PRODUCT
}
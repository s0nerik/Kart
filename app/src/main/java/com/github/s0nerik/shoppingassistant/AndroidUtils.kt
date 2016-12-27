package com.github.s0nerik.shoppingassistant

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.support.annotation.NonNull


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
fun Int.getDrawableUri(@NonNull context: Context): Uri {
    val imageUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.resources.getResourcePackageName(this)}/${context.resources.getResourceTypeName(this)}/${context.resources.getResourceEntryName(this)}"
    )
    return imageUri
}
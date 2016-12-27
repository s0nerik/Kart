package com.github.s0nerik.shoppingassistant

import android.app.Application
import android.graphics.Color
import com.github.s0nerik.glide_bindingadapter.GlideBindingConfig
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.wasabeef.glide.transformations.ColorFilterTransformation

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(
                RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
        )
        createDummyPurchases(this)
        configureGlide()
    }

    // TODO: figure out what to do with local vector drawables not being loaded
    private fun configureGlide() {
        GlideBindingConfig.registerProvider("purchase_icon", { iv, request ->
            request.bitmapTransform(ColorFilterTransformation(this, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                    .error(R.drawable.alert_circle_outline)
        })
    }
}
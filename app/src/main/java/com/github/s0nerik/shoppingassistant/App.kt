package com.github.s0nerik.shoppingassistant

import android.app.Application
import android.graphics.Color
import android.util.Log
import com.github.s0nerik.glide_bindingadapter.GlideBindingConfig
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import timber.log.Timber



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
        configureTimber()
    }

    // TODO: figure out what to do with local vector drawables not being loaded
    private fun configureGlide() {
        GlideBindingConfig.registerProvider("purchase_icon", { iv, request ->
            request.bitmapTransform(ColorFilterTransformation(this, if (iv.imageTintList != null) iv.imageTintList.defaultColor else Color.BLACK))
                    .error(R.drawable.alert_circle_outline)
        })
    }

    private fun configureTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    /** A tree which logs important information for crash reporting. */
    class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

// TODO: uncomment this when Crashlytics added
//                FakeCrashLibrary.log(priority, tag, message)
//
//                if (t != null) {
//                    if (priority == Log.ERROR) {
//                        FakeCrashLibrary.logError(t)
//                    } else if (priority == Log.WARN) {
//                        FakeCrashLibrary.logWarning(t)
//                    }
//                }
        }
    }
}
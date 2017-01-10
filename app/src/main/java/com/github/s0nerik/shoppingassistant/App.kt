package com.github.s0nerik.shoppingassistant

import android.app.Application
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
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
        initDatabase(this, true)
        configureGlide()
        configureTimber()
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
package com.github.s0nerik.shoppingassistant

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.facebook.stetho.Stetho
import com.github.s0nerik.shoppingassistant.jobs.UpdateExchangeRatesJob
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import rx_activity_result.RxActivityResult
import timber.log.Timber



/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class App : Application() {
    companion object {
        private lateinit var ctx: Context

        val context: Context
            get() = ctx
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        RxActivityResult.register(this)

        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            override fun onActivityPaused(activity: Activity?) {}
            override fun onActivityResumed(activity: Activity?) {}
            override fun onActivityStarted(activity: Activity?) {}
            override fun onActivityDestroyed(activity: Activity?) {}
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
            override fun onActivityStopped(activity: Activity?) {}
        })

        Realm.init(this)
        Realm.setDefaultConfiguration(
                RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
        )
        initDatabase(this, true, true, true, true, true)
        configureGlide()
        configureTimber()

        UpdateExchangeRatesJob.schedule(this)
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

class DebugApp : App() {
    override fun onCreate() {
        initStrictMode()
        super.onCreate()
        initStetho()
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                                           .detectAll()
                                           .penaltyLog()
                                           .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                                       .detectAll()
                                       .penaltyLog()
                                       .penaltyDeath()
                                       .build())
    }

    private fun initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build()
        )
    }
}
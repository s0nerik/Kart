package com.github.s0nerik.shoppingassistant

import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider

class DebugApp : App() {
    override fun onCreate() {
        initStrictMode()
        super.onCreate()
        initStetho()
    }

    override fun initDatabase() {
//        Db.initDatabase(this, false, true, true, true, true)
        Db.initDatabase(this, true, true, true, true, true)
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
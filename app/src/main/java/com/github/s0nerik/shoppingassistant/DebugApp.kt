package com.github.s0nerik.shoppingassistant

import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider

/**
 * Created by Alex on 1/24/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DebugApp : App() {
    override fun onCreate() {
        super.onCreate()
        initStetho()
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
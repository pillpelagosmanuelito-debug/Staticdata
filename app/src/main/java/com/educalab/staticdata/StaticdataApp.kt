package com.educalab.staticdata

import android.app.Application
import com.educalab.staticdata.data.local.seed.SeedProvider
import com.educalab.staticdata.util.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StaticdataApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        applicationScope.launch {
            SeedProvider.seedIfEmpty(container.database)
        }
    }
}

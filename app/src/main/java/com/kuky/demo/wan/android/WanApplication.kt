package com.kuky.demo.wan.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.kuky.demo.wan.android.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @author kuky.
 * @description
 */
class WanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = applicationContext

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@WanApplication)
            androidFileProperties()
            modules(
                dataSourceModule, repositoryModule, viewModelModule,
                fragmentModule, dialogModule, adapterModule
            )
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context
    }
}
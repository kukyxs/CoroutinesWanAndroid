package com.kuky.demo.wan.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @author kuky.
 * @description
 */
class WanApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context
    }
}
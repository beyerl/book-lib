package com.lspace.booklib

import android.app.Application
import com.lspace.booklib.di.AppContainer

class BookLibApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

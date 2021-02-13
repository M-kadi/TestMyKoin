package com.example.testmykoin

import android.app.Application
import com.example.testmykoin.dataModule.MyViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)

            modules(listOf(MyModule, MyViewModelModule))
        }
    }
}




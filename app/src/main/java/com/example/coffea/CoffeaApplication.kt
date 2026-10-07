package com.example.coffea

import android.app.Application
import com.example.coffea.data.AppContainer
import com.example.coffea.data.AppDataContainer

class CoffeaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
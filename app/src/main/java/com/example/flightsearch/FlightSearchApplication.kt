package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.AppDataContainer

class FlightSearchApplication : Application() {

    /**
     * AppContainer instance providing access to repositories
     * Used by the rest of the app for dependency injection
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }
}
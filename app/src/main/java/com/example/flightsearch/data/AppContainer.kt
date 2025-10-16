package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private  const val SEARCH_QUERY_NAME = "search_query"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = SEARCH_QUERY_NAME)


interface AppContainer {
    val airportRepository : AirportRepository
     val favoriteRepository : FavoriteRepository
     val userPreferenceRepository : UserPreferenceRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val airportRepository: AirportRepository by lazy {
        AirportRepositoryImpl(FlightSearchDatabase.getDatabase(context).airportDao())
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepositoryImpl(FlightSearchDatabase.getDatabase(context).favoriteDao())
    }

    override val userPreferenceRepository: UserPreferenceRepository by lazy {
        UserPreferenceRepository(context.datastore)
    }

}
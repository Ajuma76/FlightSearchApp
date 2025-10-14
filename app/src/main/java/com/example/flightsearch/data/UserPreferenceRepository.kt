package com.example.flightsearch.data


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map



class UserPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val SEARCH_QUERY = stringPreferencesKey("search_query")
        const val  TAG = "UserPreferencesRepo"
    }

    //get the saved search query as a flow
    val searchQuery: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            preferences ->
            preferences[SEARCH_QUERY] ?: ""
        }


    //save the search query to datastore
    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY] = query
        }
    }

    //Clear the saved search query
    suspend fun clearSearchQuery() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_QUERY)
        }
    }

}
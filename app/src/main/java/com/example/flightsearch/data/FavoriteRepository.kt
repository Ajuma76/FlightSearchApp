package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun getAllFavoritesRoutes() : Flow<List<Favorite>>

    //add a route to favorite
    suspend fun addFavorite(departureCode: String, destinationCode: String)

    suspend fun removeFavorite(departureCode: String, destinationCode: String)

    suspend fun isFavorite(departureCode: String, destinationCode: String) : Boolean

    //Get favorite by route codes
    suspend fun getFavoriteByRoute(departureCode: String, destinationCode: String): Favorite?

}
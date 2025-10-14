package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl(private val favoriteDao: FavoriteDao) : FavoriteRepository {

    override fun getAllFavoritesRoutes(): Flow<List<Favorite>> =
        favoriteDao.getAllFavoriteRoutes()

    override suspend fun addFavorite(departureCode: String, destinationCode: String) {
        val favorite = Favorite(
            departureCode = departureCode,
            destinationCode = destinationCode
        )
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun removeFavorite(departureCode: String, destinationCode: String) =
        favoriteDao.deleteFavoriteByRoute(departureCode, destinationCode)

    override suspend fun isFavorite(departureCode: String, destinationCode: String) =
        favoriteDao.isFavorite(departureCode, destinationCode)

    override suspend fun getFavoriteByRoute(departureCode: String, destinationCode: String): Favorite? =
        favoriteDao.getFavoriteByRoute(departureCode, destinationCode)

}


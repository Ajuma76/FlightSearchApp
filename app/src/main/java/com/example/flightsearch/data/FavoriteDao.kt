package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteDao {

    //Get all favorite routes as Flow
    @Query("SELECT * from favorite ORDER BY id DESC")
    fun getAllFavoriteRoutes(): Flow<List<Favorite>>

    //save favourite route
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    // Delete a favorite route
    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    //Check if a route is already favorite (for star toggle logic)
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM favorite
            WHERE departure_code = :departureCode AND destination_code = :destinationCode
        )
    """)
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean


    //Delete favorite by route code
    @Query("""
        DELETE FROM favorite
        WHERE departure_code = :departureCode
        AND destination_code = :destinationCode
    """)
    suspend fun deleteFavoriteByRoute(departureCode: String, destinationCode: String)

    //Get favorite by route code
    @Query("""
        SELECT * FROM favorite
        WHERE departure_code = :departureCode
        AND destination_code = :destinationCode
        LIMIT 1
    """)
    suspend fun getFavoriteByRoute(departureCode: String, destinationCode: String) : Favorite?
}

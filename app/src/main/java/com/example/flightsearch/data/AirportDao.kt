package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface AirportDao {

//Autocomplete suggestion
    @Query(
        """SELECT * FROM airport 
            WHERE name LIKE '%' || :searchQuery || '%' OR iata_code LIKE '%' || :searchQuery || '%'
            ORDER BY passengers DESC""")
    fun searchAirports(searchQuery: String) : Flow<List<Airport>>

    //Destinations from selected airport(excluding itself)
    @Query(
        """SELECT * FROM airport 
                WHERE iata_code != :iataCode 
                ORDER BY passengers DESC"""
    )
    fun getDestinationFrom(iataCode: String) : Flow<List<Airport>>

    /**
     * Get single airport by IATA code
     */
    @Query("SELECT * FROM airport WHERE iata_code = :iataCode LIMIT 1")
    suspend fun getAirportByCode(iataCode: String): Airport?

    /**
     * Get all airports (for testing purposes)
     */
    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

}
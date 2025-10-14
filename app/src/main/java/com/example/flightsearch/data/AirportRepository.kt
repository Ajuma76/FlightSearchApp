package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    /**
     * Search airports with autocomplete
     * Returns Flow for reactive updates
     */
    fun searchAirports(query: String) : Flow<List<Airport>>

    /**
     * Get all available flights from a specific airport
     * Excludes the departure airport itself
     */
    fun getDestinationFrom(departureCode: String) : Flow<List<Airport>>

    /**
     * Get airport details by IATA code
     */
    suspend fun getAirportsByCode(code: String) : Airport?

    //Get all airports
    fun getAllAirports() : Flow<List<Airport>>
}
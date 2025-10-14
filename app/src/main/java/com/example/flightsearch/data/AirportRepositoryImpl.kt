package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class AirportRepositoryImpl(private val airportDao: AirportDao) : AirportRepository {

    override fun searchAirports(query: String): Flow<List<Airport>> =
        airportDao.searchAirports(query)

    override fun getDestinationFrom(departureCode: String): Flow<List<Airport>> =
        airportDao.getDestinationFrom(departureCode)

    override suspend fun getAirportsByCode(code: String): Airport? =
        airportDao.getAirportByCode(code)

    override fun getAllAirports(): Flow<List<Airport>> =
        airportDao.getAllAirports()

}
package com.example.flightsearch.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.ui.Airport.AirportViewModel
import com.example.flightsearch.ui.Favorite.FavoriteViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            AirportViewModel(
                airportRepository = flightSearchApplication().container.airportRepository,
                userPreferenceRepository = flightSearchApplication().container.userPreferenceRepository
            )
        }

        initializer {
            FavoriteViewModel(
                favoriteRepository = flightSearchApplication().container.favoriteRepository
            )
        }
    }
}

fun CreationExtras.flightSearchApplication() : FlightSearchApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
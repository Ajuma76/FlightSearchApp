package com.example.flightsearch.ui.Airport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AirportViewModel(
    private val airportRepository: AirportRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val autoCompleteSuggestions: StateFlow<List<Airport>> = _searchQuery
        .flatMapLatest { query->
            if (query.isBlank()){
                flowOf(emptyList())
            } else {
                airportRepository.searchAirports(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val availableFlights: StateFlow<List<Airport>> = _selectedAirport
        .flatMapLatest { airport ->
            if (airport != null){
                airportRepository.getDestinationFrom(airport.iataCode)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(AirportUiState())
    val uiState: StateFlow<AirportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                _searchQuery.collect { query ->
                    _uiState.value = _uiState.value.copy(searchQuery = query)
                }
            }
            launch {
                _selectedAirport.collect { airport ->
                    _uiState.value = _uiState.value.copy(selectedAirport = airport)
                }
            }
            launch {
                autoCompleteSuggestions.collect { suggestions ->
                    _uiState.value = _uiState.value.copy(autoCompleteSuggestions = suggestions)
                }
            }
            launch {
                availableFlights.collect { flights ->
                    _uiState.value = _uiState.value.copy(availableFlights = flights)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectedAirport(airport: Airport) {
        _selectedAirport.value = airport
        _searchQuery.value = ""
    }

    fun clearSelection() {
        _selectedAirport.value = null
        _searchQuery.value = ""
    }
}


data class AirportUiState(
    val searchQuery: String = "",
    val selectedAirport: Airport? = null,
    val autoCompleteSuggestions: List<Airport> = emptyList(),
    val availableFlights: List<Airport> = emptyList()
)
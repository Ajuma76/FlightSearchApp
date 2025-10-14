package com.example.flightsearch.ui.Favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favoriteRoutes: StateFlow<List<Favorite>> = favoriteRepository.getAllFavoritesRoutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _favoriteStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState : StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRoutes.collect { favorites ->
                _uiState.value = _uiState.value.copy(favoriteRoutes = favorites)
            }
        }
    }

    fun addFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            favoriteRepository.addFavorite(departureCode, destinationCode)
            updateFavoriteStatusInMap(departureCode, destinationCode, true)
        }
    }

    fun removeFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(departureCode, destinationCode)
            updateFavoriteStatusInMap(departureCode, destinationCode, false)
        }
    }

    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            val isFav = favoriteRepository.isFavorite(departureCode, destinationCode)
            if (isFav){
                removeFavorite(departureCode, destinationCode)
            } else {
                addFavorite(departureCode, destinationCode)
            }
        }
    }


    fun updateFavoriteStatusForRoutes(
        departureCode: String,
        destinations: List<String>
    ) {
        viewModelScope.launch {
            val statusMap = _uiState.value.favoriteStatus.toMutableMap()
            destinations.forEach { destinationCode ->
                val key = "$departureCode-$destinationCode"
                val status = favoriteRepository.isFavorite(departureCode, destinationCode)
                statusMap[key] = status
            }
            _uiState.value = _uiState.value.copy(favoriteStatus = statusMap)
        }
    }


    private fun updateFavoriteStatusInMap(
        departureCode: String,
        destinationCode: String,
        isFavorite: Boolean
    ) {
        val key = "$departureCode - $destinationCode"
        val newStatus = _uiState.value.favoriteStatus.toMutableMap()
        newStatus[key] = isFavorite
        _uiState.value = _uiState.value.copy(favoriteStatus = newStatus)
    }
}


data class FavoriteUiState (
    val favoriteRoutes: List<Favorite> = emptyList(),
    val favoriteStatus: Map<String, Boolean> = emptyMap()
)

//    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean {
//        val key = "$departureCode-$destinationCode"
//        val cachedStatus = _uiState.value.favoriteStatus[key]
//        if (cachedStatus != null) {
//            return cachedStatus
//        }
//
//        val status = favoriteRepository.isFavorite(departureCode, destinationCode)
//        updateFavoriteStatusInMap(departureCode, destinationCode, status)
//        return status
//    }
//
//    fun getFavoriteStatus(departureCode: String, destinationCode: String): Boolean {
//        val key = "$departureCode-$destinationCode"
//        return _uiState.value.favoriteStatus[key] ?: false
//    }

//    private fun updateFavoriteStatusMap(favorite: List<Favorite>) {
//        val statusMap = favorite.associate { favorite ->
//            val key = "${favorite.departureCode} - ${favorite.destinationCode}"
//            key to true
//        }
//    }

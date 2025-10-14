package com.example.flightsearch.ui.Airport



import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.FlightSearchTopAppBar
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.AppViewModelProvider
import com.example.flightsearch.ui.Favorite.FavoriteContent
import com.example.flightsearch.ui.Favorite.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportSearchScreen(
    modifier: Modifier = Modifier,
    airportViewModel: AirportViewModel = viewModel(factory = AppViewModelProvider.Factory),
    favoriteViewModel: FavoriteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val airportUiState by airportViewModel.uiState.collectAsState()
    val favoriteUiState by favoriteViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(airportUiState.selectedAirport, airportUiState.availableFlights) {
        airportUiState.selectedAirport ?.let { departure ->
            if (airportUiState.availableFlights.isNotEmpty()){
                favoriteViewModel.updateFavoriteStatusForRoutes(
                    departureCode = departure.iataCode,
                    destinations = airportUiState.availableFlights.map { it.iataCode }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            FlightSearchTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValue ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValue)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            SearchTextField(
                searchQuery = airportUiState.searchQuery,
                onQueryChange = { airportViewModel.updateSearchQuery(it) },
                onClearClick = { airportViewModel.clearSelection() },
                showClearButton = airportUiState.selectedAirport != null
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            Box (modifier = Modifier.fillMaxSize()){
                when {
                    airportUiState.selectedAirport != null -> {
                        FlightResultContent(
                            departureAirport = airportUiState.selectedAirport!!,
                            flights = airportUiState.availableFlights,
                            favoriteStatus = favoriteUiState.favoriteStatus,
                            onFavoriteClick = { destination ->
                                favoriteViewModel.toggleFavorite(
                                    airportUiState.selectedAirport!!.iataCode,
                                    destination.iataCode
                                )
                            }
                        )
                    }
                    airportUiState.searchQuery.isBlank() -> {
                        FavoriteContent(
                            favorites = favoriteUiState.favoriteRoutes,
                            onDeleteClick = { favorite ->
                                favoriteViewModel.removeFavorite(
                                    favorite.departureCode,
                                    favorite.destinationCode
                                )
                            }
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = airportUiState.autoCompleteSuggestions.isNotEmpty() &&
                    airportUiState.searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AutocompleteContent(
                            suggestions = airportUiState.autoCompleteSuggestions,
                            onAirportClick = { airport ->
                                airportViewModel.selectedAirport(airport)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchTextField(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    showClearButton: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { stringResource(R.string.enter_departure_airport) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon))
        },
        trailingIcon = {
            if (showClearButton) {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun AutocompleteContent(
    suggestions: List<Airport>,
    onAirportClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(suggestions, key = {it.id}){ airport ->
            AirportItem(
                airport = airport,
                onClick = { onAirportClick(airport) }
            )
        }
    }
}

@Composable
fun AirportItem(
    airport: Airport,
    onClick:() -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column (
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ){
            Text(
                text = airport.iataCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FlightResultContent(
    departureAirport: Airport,
    flights: List<Airport>,
    favoriteStatus: Map<String, Boolean>,
    onFavoriteClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.flights_from),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${departureAirport.iataCode} - ${departureAirport.name}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_extra_small))
        )

        LazyColumn {
            items(flights, key = {it.id}) { destination ->
                FlightItem(
                    departureCode = departureAirport.iataCode,
                    destination = destination,
                    isFavorite = favoriteStatus["${departureAirport.iataCode} - ${destination.iataCode}"] ?: false,
                    onFavoriteClick = { onFavoriteClick(destination) }
                )
            }
        }
    }
}

@Composable
fun FlightItem(
    departureCode: String,
    destination: Airport,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.iataCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites)
                        else stringResource(R.string.add_to_favorites),
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
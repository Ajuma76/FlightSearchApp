package com.example.flightsearch.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite


@Composable
fun FavoritesContent(
    favorites: List<Favorite>,
    onToggleFavorite: (String, String) -> Unit,
    airportViewModel: AirportViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.favorite_route),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_extra_small))
        )

        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorite_route),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
        } else {
            LazyColumn {
                items(favorites, key = {it.id}) { favorite ->
                    ExpandableFavoriteItem(
                        favorite = favorite,
                        onToggleFavorite = onToggleFavorite,
                        airportViewModel = airportViewModel
                    )
                }
            }
        }
    }
}


@Composable
fun ExpandableFavoriteItem(
    favorite: Favorite,
    onToggleFavorite: (String, String) -> Unit,
    airportViewModel: AirportViewModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var departureAirport by remember { mutableStateOf<Airport?>(null) }
    var destinationAirport by remember { mutableStateOf<Airport?>(null) }

    //Load airport details when component mounts
    LaunchedEffect(favorite.id) {
        departureAirport = airportViewModel.getAirportByCode(favorite.departureCode)
        destinationAirport = airportViewModel.getAirportByCode(favorite.destinationCode)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ){
            //left side....
            Column (
                modifier = modifier
                    .weight(1f)
                //add spring animation for content size change
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                if (isExpanded) {
                    //Expanded view: Route Information
                    ExpandedRouteDetails(
                        departureAirport = departureAirport,
                        destinationAirport = destinationAirport,
                        departureCode = favorite.departureCode,
                        destinationCode = favorite.departureCode
                    )
                } else {
                    //show iata codes only
                    CollapsedRouteDetails(
                        departureCode = favorite.departureCode,
                        destinationCode = favorite.destinationCode
                    )
                }
            }

            //Right side
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val iconSize = dimensionResource(R.dimen.iconSize)

                IconButton(
                    onClick = {
                        onToggleFavorite(favorite.departureCode, favorite.destinationCode)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.remove_from_favorites),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(iconSize)
                    )
                }

                // 2. Arrow Icon dependent on isExpanded
                IconButton(onClick = {isExpanded = !isExpanded}) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(
                            R.string.expand
                        ),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

@Composable
fun CollapsedRouteDetails(
    departureCode: String,
    destinationCode: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = departureCode,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "→",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = destinationCode,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExpandedRouteDetails(
    departureAirport: Airport?,
    destinationAirport: Airport?,
    departureCode: String,
    destinationCode: String,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier){
        Text(
            text = stringResource(R.string.depart),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = departureAirport?.name ?: departureCode,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        //Arrow divider
        Text(
            text = "↓",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = stringResource(R.string.arrive),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = destinationAirport?.name ?: destinationCode,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )

    }
}
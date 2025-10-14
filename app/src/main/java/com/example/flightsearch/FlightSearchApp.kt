package com.example.flightsearch

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors()
) {
    MediumTopAppBar(
        title = {
            // Calculate collapse fraction (0f = expanded, 1f = collapsed)
            val collapsedFraction = scrollBehavior?.state?.collapsedFraction ?: 0f

            // Interpolate text style: larger when expanded, smaller when collapsed
            val textStyle = lerp(
                start = MaterialTheme.typography.headlineSmall,  // Expanded: larger font
                stop = MaterialTheme.typography.titleLarge,     // Collapsed: smaller font
                fraction = collapsedFraction
            )

            // Interpolate color: onPrimary (e.g., white) expanded, onSurface (e.g., black) collapsed
            val textColor = lerp(
                start = MaterialTheme.colorScheme.primary,
                stop = MaterialTheme.colorScheme.primary,  // Change to primary if you want colored text on scrolled bg
                fraction = collapsedFraction
            )

            Text(
                text = title,
                style = textStyle,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack){

                val collapsedFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
                val iconColor = lerp(
                    start = MaterialTheme.colorScheme.onPrimary,
                    stop = MaterialTheme.colorScheme.onSurface,
                    fraction = collapsedFraction
                )

                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = iconColor
                    )
                }
            }
        },
        colors = colors
    )
}
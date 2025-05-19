package org.nomad.mapapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory

private val LightColors = lightColorScheme(
    primary = Color(0xFF006E1C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF95F990),
    onPrimaryContainer = Color(0xFF002204),
    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF97F0FF),
    onSecondaryContainer = Color(0xFF001F24),
    tertiary = Color(0xFF006874),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF97F0FF),
    onTertiaryContainer = Color(0xFF001F24),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCFDF6),
    onBackground = Color(0xFF1A1C19),
    surface = Color(0xFFFCFDF6),
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFDEE5D8),
    onSurfaceVariant = Color(0xFF424940),
    outline = Color(0xFF72796F),
    inverseOnSurface = Color(0xFFF1F1EB),
    inverseSurface = Color(0xFF2F312D)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF79DC77),
    onPrimary = Color(0xFF00390C),
    primaryContainer = Color(0xFF005314),
    onPrimaryContainer = Color(0xFF95F990),
    secondary = Color(0xFF4FD8EB),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFF97F0FF),
    tertiary = Color(0xFF4FD8EB),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF004F58),
    onTertiaryContainer = Color(0xFF97F0FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C9BD),
    outline = Color(0xFF8C9388),
    inverseOnSurface = Color(0xFF1A1C19),
    inverseSurface = Color(0xFFE2E3DD)
)

object MapMarkerColors {
    data class MarkerColorSet(
        val retail: Float,
        val provider: Float,
        val both: Float,
        val default: Float
    )

    val defaultColors = MarkerColorSet(
        retail = BitmapDescriptorFactory.HUE_BLUE,
        provider = BitmapDescriptorFactory.HUE_RED,
        both = BitmapDescriptorFactory.HUE_GREEN,
        default = BitmapDescriptorFactory.HUE_YELLOW
    )

    val daltonismColors = MarkerColorSet(
        retail = BitmapDescriptorFactory.HUE_AZURE,
        provider = BitmapDescriptorFactory.HUE_ORANGE,
        both = BitmapDescriptorFactory.HUE_MAGENTA,
        default = BitmapDescriptorFactory.HUE_YELLOW
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

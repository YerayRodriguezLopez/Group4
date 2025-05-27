package org.nomad.mapapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.nomad.mapapp.R
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.ui.component.FilterDialog
import org.nomad.mapapp.ui.component.MapBottomBar
import org.nomad.mapapp.ui.navigation.Screen
import org.nomad.mapapp.ui.theme.MapMarkerColors
import org.nomad.mapapp.ui.viewmodel.MapViewModel

class CompanyClusterItem(
    val company: Company,
    private val position: LatLng = company.address?.toLatLng() ?: LatLng(0.0, 0.0)
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = company.name
    override fun getSnippet(): String = "${company.address?.location ?: ""} - ★ ${company.getDisplayScore()}"
    override fun getZIndex(): Float = 0f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    val companies by viewModel.companies.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val lastMapPosition by viewModel.lastMapPosition.collectAsState()
    val isDaltonismMode by viewModel.isDaltonismMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            lastMapPosition ?: LatLng(41.3851, 2.1734), // Barcelona default
            12f
        )
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false
            )
        )
    }

    val scope = rememberCoroutineScope()
    var showFilterDialog by remember { mutableStateOf(false) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            scope.launch {
                getCurrentLocation(context) { location ->
                    scope.launch {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(location, 15f)
                        )
                        viewModel.updateMapPosition(location)
                        viewModel.fetchCompaniesNearby(location.latitude.toFloat(), location.longitude.toFloat())
                    }
                }
            }
        }
    }

    LaunchedEffect(cameraPositionState.position) {
        viewModel.updateMapPosition(cameraPositionState.position.target)
    }

    // Debug: Log companies state
    LaunchedEffect(companies) {
        println("MapScreen: Companies updated, count = ${companies.size}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDaltonismMode() }) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = stringResource(R.string.daltonism_mode)
                        )
                    }
                }
            )
        },
        bottomBar = {
            MapBottomBar(
                navController = navController,
                currentRoute = Screen.Map.route
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                val filteredCompanies = viewModel.getFilteredCompanies()
                val clusterItems = filteredCompanies.mapNotNull { company ->
                    company.address?.let {
                        println("Creating cluster item for company: ${company.name} at ${it.lat}, ${it.lng}")
                        CompanyClusterItem(company)
                    }
                }

                println("MapScreen: Rendering ${clusterItems.size} cluster items")

                if (clusterItems.isNotEmpty()) {
                    Clustering(
                        items = clusterItems,
                        onClusterClick = { cluster ->
                            scope.launch {
                                val bounds = cluster.items.map { it.position }
                                val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.builder()
                                bounds.forEach { boundsBuilder.include(it) }
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100)
                                )
                            }
                            true
                        },
                        onClusterItemClick = { clusterItem ->
                            navController.navigate(Screen.CompanyDetails.createRoute(clusterItem.company.id.toString()))
                            true
                        },
                        clusterItemContent = { clusterItem ->
                            val company = clusterItem.company
                            val markerColors = if (isDaltonismMode) {
                                MapMarkerColors.daltonismColors
                            } else {
                                MapMarkerColors.defaultColors
                            }

                            val markerColor = when {
                                company.isRetail && company.isProvider -> markerColors.both
                                company.isRetail -> markerColors.retail
                                company.isProvider -> markerColors.provider
                                else -> markerColors.default
                            }

                            MarkerInfoWindow(
                                state = rememberMarkerState(position = clusterItem.position),
                                title = company.name,
                                snippet = clusterItem.snippet,
                                icon = BitmapDescriptorFactory.defaultMarker(markerColor)
                            )
                        }
                    )
                }
            }

            // Location button (top left)
            FloatingActionButton(
                onClick = {
                    val permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    val hasPermission = permissions.any { permission ->
                        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    }

                    if (hasPermission) {
                        scope.launch {
                            getCurrentLocation(context) { location ->
                                scope.launch {
                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngZoom(location, 15f)
                                    )
                                    viewModel.updateMapPosition(location)
                                    viewModel.fetchCompaniesNearby(location.latitude.toFloat(), location.longitude.toFloat())
                                }
                            }
                        }
                    } else {
                        locationPermissionLauncher.launch(permissions)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.my_location),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            CircleShape
                        )
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                }
            }

            // Debug info
            if (companies.isEmpty() && !isLoading) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No companies loaded. Check API connection.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Color legend
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    LegendItem(
                        color = if (isDaltonismMode)
                            MapMarkerColors.daltonismColors.retail
                        else
                            MapMarkerColors.defaultColors.retail,
                        text = stringResource(R.string.stores)
                    )
                    LegendItem(
                        color = if (isDaltonismMode)
                            MapMarkerColors.daltonismColors.provider
                        else
                            MapMarkerColors.defaultColors.provider,
                        text = stringResource(R.string.providers)
                    )
                    LegendItem(
                        color = if (isDaltonismMode)
                            MapMarkerColors.daltonismColors.both
                        else
                            MapMarkerColors.defaultColors.both,
                        text = stringResource(R.string.both)
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            selectedTags = selectedTags,
            onTagsSelected = { viewModel.updateSelectedTags(it) },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun LegendItem(color: Float, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = when (color) {
                        BitmapDescriptorFactory.HUE_RED -> Color.Red
                        BitmapDescriptorFactory.HUE_BLUE -> Color.Blue
                        BitmapDescriptorFactory.HUE_GREEN -> Color.Green
                        BitmapDescriptorFactory.HUE_AZURE -> Color(0xFF007FFF)
                        BitmapDescriptorFactory.HUE_YELLOW -> Color.Yellow
                        BitmapDescriptorFactory.HUE_ORANGE -> Color(0xFFFF8C00)
                        BitmapDescriptorFactory.HUE_MAGENTA -> Color.Magenta
                        else -> Color.Gray
                    },
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

private suspend fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                onLocationReceived(LatLng(location.latitude, location.longitude))
            }
        }
    } catch (e: Exception) {
        println("Location error: ${e.message}")
    }
}
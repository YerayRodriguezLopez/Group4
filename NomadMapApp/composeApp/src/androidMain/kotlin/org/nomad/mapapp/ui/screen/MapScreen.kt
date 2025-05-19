package org.nomad.mapapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering
import kotlinx.coroutines.launch
import org.nomad.mapapp.R
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.ui.component.FilterDialog
import org.nomad.mapapp.ui.component.MapBottomBar
import org.nomad.mapapp.ui.component.SearchBar
import org.nomad.mapapp.ui.navigation.Screen
import org.nomad.mapapp.ui.theme.MapMarkerColors
import org.nomad.mapapp.ui.viewmodel.MapViewModel

class CompanyClusterItem(
    val company: Company,
    private val position: LatLng = LatLng(
        company.address.coordinates.latitude,
        company.address.coordinates.longitude
    )
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = company.name
    override fun getSnippet(): String = company.address.location
    override fun getZIndex(): Float = 0f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel
) {
    val companies by viewModel.companies.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    val lastMapPosition by viewModel.lastMapPosition.collectAsState()
    val isDaltonismMode by viewModel.isDaltonismMode.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            lastMapPosition ?: LatLng(41.3851, 2.1734), // Barcelona default
            12f
        )
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        )
    }

    val scope = rememberCoroutineScope()
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Company>>(emptyList()) }
    var showSearchResults by remember { mutableStateOf(false) }

    LaunchedEffect(cameraPositionState.position) {
        viewModel.updateMapPosition(cameraPositionState.position.target)
    }

    LaunchedEffect(searchText) {
        if (searchText.length >= 2) {
            searchResults = companies.filter {
                it.name.contains(searchText, ignoreCase = true)
            }
            showSearchResults = searchResults.isNotEmpty()
        } else {
            showSearchResults = false
        }
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
            Column {
                SearchBar(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = stringResource(R.string.search_placeholder),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )

                if (showSearchResults) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(searchResults) { company ->
                            ListItem(
                                headlineContent = { Text(company.name) },
                                supportingContent = { Text(company.address.location) },
                                modifier = Modifier.clickable {
                                    // Navigate to company details
                                    scope.launch {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                    company.address.coordinates.latitude,
                                                    company.address.coordinates.longitude
                                                ),
                                                15f
                                            )
                                        )
                                    }
                                    showSearchResults = false
                                    searchText = ""
                                }
                            )
                            Divider()
                        }
                    }
                }

                Slider(
                    value = searchRadius.toFloat(),
                    onValueChange = { viewModel.updateSearchRadius(it.toDouble()) },
                    valueRange = 1f..20f,
                    steps = 19,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)//,
                    //colors = SliderColors(inactiveTickColor = Color.Transparent)
                )

                Text(
                    text = stringResource(R.string.radius_km, searchRadius.toInt()),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        onMapLoaded = {
                            viewModel.fetchCompaniesNearby(
                                cameraPositionState.position.target.latitude,
                                cameraPositionState.position.target.longitude
                            )
                        }
                    ) {
                        val filteredCompanies = viewModel.getFilteredCompanies()
                        val clusterItems = filteredCompanies.map { CompanyClusterItem(it) }

                        Clustering(
                            items = clusterItems,
                            onClusterClick = { cluster ->
                                // Show list of companies in this cluster
                                true // consume the event
                            },
                            onClusterItemClick = { clusterItem ->
                                // Navigate to company details
                                navController.navigate(Screen.CompanyDetails.createRoute(clusterItem.company.id))
                                true // consume the event
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
                                    snippet = company.address.location,
                                    icon = BitmapDescriptorFactory.defaultMarker(markerColor)
                                )
                            }
                        )
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
                        // Add other colors as needed
                        else -> Color.Gray
                    },
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

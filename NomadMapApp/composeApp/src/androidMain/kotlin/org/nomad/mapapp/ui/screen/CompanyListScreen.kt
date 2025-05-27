package org.nomad.mapapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.nomad.mapapp.R
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.ui.component.FilterDialog
import org.nomad.mapapp.ui.component.MapBottomBar
import org.nomad.mapapp.ui.navigation.Screen
import org.nomad.mapapp.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListScreen(
    navController: NavController,
    viewModel: MapViewModel,
    onCompanySelected: (Company) -> Unit
) {
    val context = LocalContext.current
    val companies by viewModel.companies.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var sortedCompanies by remember { mutableStateOf<List<Company>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // Get user location and sort companies by proximity
    LaunchedEffect(companies) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await()

            if (location != null) {
                sortedCompanies = viewModel.getCompaniesSortedByProximity(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
            } else {
                sortedCompanies = companies
            }
        } catch (e: Exception) {
            sortedCompanies = companies
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.company_list)) },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter)
                        )
                    }
                }
            )
        },
        bottomBar = {
            MapBottomBar(
                navController = navController,
                currentRoute = Screen.List.route
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else if (sortedCompanies.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(R.string.no_companies_found))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedCompanies) { company ->
                        ListItem(
                            headlineContent = { Text(company.name) },
                            supportingContent = {
                                Column {
                                    company.address?.let { address ->
                                        Text(address.location)
                                    } ?: Text(stringResource(R.string.address_unavailable))

                                    Text(
                                        stringResource(
                                            when {
                                                company.isRetail && company.isProvider -> R.string.both
                                                company.isRetail -> R.string.store
                                                company.isProvider -> R.string.provider
                                                else -> R.string.unknown
                                            }
                                        )
                                    )
                                }
                            },
                            trailingContent = {
                                Text("★ ${company.getDisplayScore()}")
                            },
                            modifier = Modifier
                                .clickable { onCompanySelected(company) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider()
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
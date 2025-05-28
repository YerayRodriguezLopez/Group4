package org.nomad.mapapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nomad.mapapp.R
import org.nomad.mapapp.ui.viewmodel.CompanyDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailsScreen(
    navController: NavController,
    viewModel: CompanyDetailsViewModel
) {
    val company by viewModel.selectedCompany.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(company?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (company == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = company!!.name,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        HorizontalDivider()

                        Row {
                            Text(
                                text = stringResource(R.string.address),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = company!!.address?.location ?: stringResource(R.string.address_unavailable),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row {
                            Text(
                                text = stringResource(R.string.phone),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = company!!.getPhoneAsString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row {
                            Text(
                                text = stringResource(R.string.email_label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = company!!.mail,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row {
                            Text(
                                text = stringResource(R.string.type),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = when {
                                    company!!.isRetail && company!!.isProvider -> stringResource(R.string.both)
                                    company!!.isRetail -> stringResource(R.string.store)
                                    company!!.isProvider -> stringResource(R.string.provider)
                                    else -> stringResource(R.string.unknown)
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row {
                            Text(
                                text = stringResource(R.string.score),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "★ ${company!!.getDisplayScore()}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.tags),
                            style = MaterialTheme.typography.titleLarge
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            company!!.getTagsList().forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
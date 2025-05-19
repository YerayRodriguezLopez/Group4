// MainActivity.kt
package org.nomad.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import org.nomad.mapapp.data.network.ApiClient
import org.nomad.mapapp.data.repository.CompanyRepository
import org.nomad.mapapp.data.repository.UserRepository
import org.nomad.mapapp.ui.navigation.AppNavigation
import org.nomad.mapapp.ui.theme.AppTheme
import org.nomad.mapapp.ui.viewmodel.CompanyDetailsViewModel
import org.nomad.mapapp.ui.viewmodel.LoginViewModel
import org.nomad.mapapp.ui.viewmodel.MapViewModel
import org.nomad.mapapp.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private val apiClient = ApiClient()
    private val companyRepository = CompanyRepository(apiClient)
    private val userRepository = UserRepository(apiClient)
    private val viewModelFactory = ViewModelFactory(companyRepository, userRepository)

    private val mapViewModel: MapViewModel by viewModels { viewModelFactory }
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private val companyDetailsViewModel: CompanyDetailsViewModel by viewModels { viewModelFactory }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Location permission granted, fetch nearby companies
            // This will be handled in the MapViewModel when the map is loaded
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request location permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
            }
            else -> {
                // Request the permission
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        mapViewModel = mapViewModel,
                        loginViewModel = loginViewModel,
                        companyDetailsViewModel = companyDetailsViewModel
                    )
                }
            }
        }
    }
}
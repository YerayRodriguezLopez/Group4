package org.nomad.mapapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nomad.mapapp.ui.screen.*
import org.nomad.mapapp.ui.viewmodel.CompanyDetailsViewModel
import org.nomad.mapapp.ui.viewmodel.LoginViewModel
import org.nomad.mapapp.ui.viewmodel.MapViewModel

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object List : Screen("list")
    object Login : Screen("login")
    object Register : Screen("register")
    object CompanyDetails : Screen("company_details/{companyId}")

    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                route.replace("{$arg}", arg)
            }
        }
    }
}

@Composable
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    mapViewModel: MapViewModel,
    loginViewModel: LoginViewModel,
    companyDetailsViewModel: CompanyDetailsViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Map.route) {
        composable(Screen.Map.route) {
            MapScreen(
                navController = navController,
                viewModel = mapViewModel,
                onCompanySelected = { company ->
                    companyDetailsViewModel.selectCompany(company)
                    navController.navigate(Screen.CompanyDetails.createRoute(company.id.toString()))
                }
            )
        }

        composable(Screen.List.route) {
            CompanyListScreen(
                navController = navController,
                viewModel = mapViewModel,
                onCompanySelected = { company ->
                    companyDetailsViewModel.selectCompany(company)
                    navController.navigate(Screen.CompanyDetails.createRoute(company.id.toString()))
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        composable(Screen.CompanyDetails.route) {
            CompanyDetailsScreen(
                navController = navController,
                viewModel = companyDetailsViewModel
            )
        }
    }
}
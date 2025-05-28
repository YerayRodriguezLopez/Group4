package org.nomad.mapapp.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import org.nomad.mapapp.R
import org.nomad.mapapp.ui.navigation.Screen

@Composable
fun MapBottomBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.List.route)
                        Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Outlined.List,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.list)) },
            selected = currentRoute == Screen.List.route,
            onClick = {
                if (currentRoute != Screen.List.route) {
                    navController.navigate(Screen.List.route) {
                        popUpTo(Screen.Map.route)
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Map.route)
                        Icons.Filled.Map else Icons.Outlined.Map,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.map)) },
            selected = currentRoute == Screen.Map.route,
            onClick = {
                if (currentRoute != Screen.Map.route) {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route)
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Login.route)
                        Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.login)) },
            selected = currentRoute == Screen.Login.route,
            onClick = {
                if (currentRoute != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Map.route)
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
package org.nomad.mapapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nomad.mapapp.R
import org.nomad.mapapp.ui.component.MapBottomBar
import org.nomad.mapapp.ui.navigation.Screen
import org.nomad.mapapp.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val loginState by viewModel.loginState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Reset login state when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.resetLoginState()
    }

    // Navigate back after successful login
    LaunchedEffect(loginState) {
        if (loginState is LoginViewModel.LoginState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        bottomBar = {
            MapBottomBar(
                navController = navController,
                currentRoute = Screen.Login.route
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentUser != null) {
                            // User is logged in
                            Text(
                                text = stringResource(R.string.welcome_user, currentUser!!.email ?: ""),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Button(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.logout))
                            }
                        } else {
                            // User needs to log in
                            Text(
                                text = stringResource(R.string.login),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    // Clear error when user starts typing
                                    if (loginState is LoginViewModel.LoginState.Error) {
                                        viewModel.resetLoginState()
                                    }
                                },
                                label = { Text(stringResource(R.string.email)) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    // Clear error when user starts typing
                                    if (loginState is LoginViewModel.LoginState.Error) {
                                        viewModel.resetLoginState()
                                    }
                                },
                                label = { Text(stringResource(R.string.password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                singleLine = true
                            )

                            // Show error or loading state
                            when (val state = loginState) {
                                is LoginViewModel.LoginState.Error -> {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                is LoginViewModel.LoginState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                is LoginViewModel.LoginState.Success -> {
                                    Text(
                                        text = "Login exitós!",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                else -> {}
                            }

                            Button(
                                onClick = { viewModel.login(email.trim(), password) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(stringResource(R.string.login))
                                }
                            }

                            TextButton(
                                onClick = { navController.navigate(Screen.Register.route) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.create_account))
                            }
                        }
                    }
                }
            }
        }
    }
}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val loginState by viewModel.loginState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                                text = stringResource(R.string.welcome_user, currentUser!!.userName),
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
                                onValueChange = { email = it },
                                label = { Text(stringResource(R.string.email_label)) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(stringResource(R.string.password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            when (loginState) {
                                is LoginViewModel.LoginState.Error -> {
                                    Text(
                                        text = (loginState as LoginViewModel.LoginState.Error).message,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                is LoginViewModel.LoginState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                else -> {}
                            }

                            Button(
                                onClick = { viewModel.login(email, password) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = email.isNotEmpty() && password.isNotEmpty() &&
                                        loginState !is LoginViewModel.LoginState.Loading
                            ) {
                                Text(stringResource(R.string.login))
                            }
                        }
                    }
                }
            }
        }
    }
}

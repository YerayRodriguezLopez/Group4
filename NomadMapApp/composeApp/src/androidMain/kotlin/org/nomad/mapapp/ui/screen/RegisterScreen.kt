package org.nomad.mapapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nomad.mapapp.R
import org.nomad.mapapp.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val registerState by viewModel.registerState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.resetRegisterState()
    }

    LaunchedEffect(key1 = registerState) {
        if (registerState is LoginViewModel.RegisterState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
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
                        Text(
                            text = stringResource(R.string.create_account),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        when (registerState) {
                            is LoginViewModel.RegisterState.Error -> {
                                Text(
                                    text = (registerState as LoginViewModel.RegisterState.Error).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            is LoginViewModel.RegisterState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is LoginViewModel.RegisterState.Success -> {
                                Text(
                                    text = stringResource(R.string.registration_success),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            else -> {}
                        }

                        Button(
                            onClick = { viewModel.register(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = email.isNotEmpty() && password.isNotEmpty() &&
                                    registerState !is LoginViewModel.RegisterState.Loading
                        ) {
                            Text(stringResource(R.string.register))
                        }
                    }
                }
            }
        }
    }
}
package org.nomad.mapapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    val isLoading by viewModel.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    // Reset register state when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.resetRegisterState()
    }

    // Navigate back after successful registration
    LaunchedEffect(registerState) {
        if (registerState is LoginViewModel.RegisterState.Success) {
            // Show success message briefly then navigate back
            kotlinx.coroutines.delay(1500)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                            onValueChange = {
                                email = it
                                // Clear error when user starts typing
                                if (registerState is LoginViewModel.RegisterState.Error) {
                                    viewModel.resetRegisterState()
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
                                if (registerState is LoginViewModel.RegisterState.Error) {
                                    viewModel.resetRegisterState()
                                }
                            },
                            label = { Text(stringResource(R.string.password)) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                // Clear error when user starts typing
                                if (registerState is LoginViewModel.RegisterState.Error) {
                                    viewModel.resetRegisterState()
                                }
                            },
                            label = { Text("Telèfon (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                // Clear error when user starts typing
                                if (registerState is LoginViewModel.RegisterState.Error) {
                                    viewModel.resetRegisterState()
                                }
                            },
                            label = { Text("Confirmar contrasenya") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true,
                            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
                        )

                        // Password validation feedback
                        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Text(
                                text = "Les contrasenyes no coincideixen",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Show register state
                        when (val state = registerState) {
                            is LoginViewModel.RegisterState.Error -> {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            is LoginViewModel.RegisterState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is LoginViewModel.RegisterState.Success -> {
                                Text(
                                    text = stringResource(R.string.registration_success),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            else -> {}
                        }

                        Button(
                            onClick = {
                                if (password == confirmPassword) {
                                    val phone = if (phoneNumber.isBlank()) null else phoneNumber.trim()
                                    viewModel.register(email.trim(), password, phone)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = email.isNotEmpty() &&
                                    password.isNotEmpty() &&
                                    confirmPassword.isNotEmpty() &&
                                    password == confirmPassword &&
                                    !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(stringResource(R.string.register))
                            }
                        }

                        TextButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            Text("Ja tinc un compte")
                        }
                    }
                }
            }
        }
    }
}
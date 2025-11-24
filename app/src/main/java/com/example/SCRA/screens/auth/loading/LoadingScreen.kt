package com.example.tire.screens.auth.loading

import AuthContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.navigation.Destination
import com.example.SCRA.screens.auth.AuthViewModel
import LoadingContent


@Composable
fun LoadingScreen1(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel._authState.observeAsState(NavHostViewModel.AuthState.LOADING)
    LoadingContent(
        authState = authState,
        navigateToAuth = { navController.navigate(Destination.AuthScreen.route) },
        navigateToSuccess = { navController.navigate(Destination.ScreenEdit.route) }
    )

    viewModel.testConnection(
        navController
    )
}


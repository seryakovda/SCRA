package com.example.tire.screens.auth.authError

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.SCRA.Destination
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.screens.auth.AuthViewModel


@Composable
fun AuthScreenError(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
    ) {
    val authState by viewModel._authState.observeAsState(NavHostViewModel.AuthState.FAIL)

    AuthContentError(
        authState = authState,
        navigateToAuthScreen = { navController.navigate(Destination.AuthScreen.route) },
        viewModel::BtnOK
    )
}

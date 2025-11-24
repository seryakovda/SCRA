package com.example.SCRA.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.screens.auth.AuthViewModel
import com.example.SCRA.screens.edit.mainEditScreen
import com.example.tire.screens.auth.authError.AuthScreenError
import com.example.tire.screens.auth.enterLogin.AuthScreen2
import com.example.tire.screens.auth.loading.LoadingScreen1

@SuppressLint("RestrictedApi")
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.AuthScreen.route
) {
    val navHostViewModel = hiltViewModel<NavHostViewModel>()
    val viewModel = hiltViewModel<AuthViewModel>()

    val state = navHostViewModel.authState.observeAsState()
    when (state.value) {
        NavHostViewModel.AuthState.LOADING -> navController.navigate(Destination.LoadingScreen.route)
        NavHostViewModel.AuthState.FAIL -> navController.navigate(Destination.AuthScreenError.route)
        NavHostViewModel.AuthState.AUTH -> navController.popBackStack()
        NavHostViewModel.AuthState.SUCCESS -> navController.navigate(Destination.ScreenMainJob.route)
        NavHostViewModel.AuthState.EDIT -> navController.navigate(Destination.ScreenEdit.route)
        else -> {}
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.LoadingScreen.route) {
            LoadingScreen1(navController = navController)
        }

        composable(Destination.AuthScreen.route) {
            AuthScreen2(navController = navController)
        }

        composable(Destination.AuthScreenError.route) {
            AuthScreenError(navController = navController)
        }

        composable(Destination.ScreenEdit.route) {
            mainEditScreen(navController = navController)
        }

    }
}
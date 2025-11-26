package com.example.SCRA.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    startDestination: String = Destination.LoadingScreen.route,
    navHostViewModel: NavHostViewModel = hiltViewModel<NavHostViewModel>()
) {
    val state = navHostViewModel.authState.observeAsState()

    LaunchedEffect(state.value) {
        when (state.value) {
            NavHostViewModel.AuthState.LOADING -> {}
                //navController.navigate(Destination.LoadingScreen.route)

            NavHostViewModel.AuthState.FAIL ->
                navController.navigate(Destination.AuthScreenError.route)

            NavHostViewModel.AuthState.AUTH ->
                navController.navigate(Destination.AuthScreen.route)

            NavHostViewModel.AuthState.SUCCESS ->
                navController.navigate(Destination.ScreenMainJob.route)

            NavHostViewModel.AuthState.EDIT ->
                navController.navigate(Destination.ScreenEdit.route)

            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        Log.i("testConnection", Destination.LoadingScreen.route)
        composable(Destination.LoadingScreen.route) {
            LoadingScreen1(
                navigateToEdit = { navController.navigate(Destination.ScreenEdit.route) },
                navigateToAuth = { navController.navigate(Destination.AuthScreen.route) }
            )
        }

        composable(Destination.AuthScreen.route) {
            AuthScreen2(navController)
        }

        composable(Destination.AuthScreenError.route) {
            AuthScreenError(navController)
        }

        composable(Destination.ScreenEdit.route) {
            mainEditScreen(navController)
        }
    }
}
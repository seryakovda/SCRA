package com.example.tire.screens.auth.enterLogin

import AuthContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.navigation.Destination
import com.example.SCRA.screens.auth.AuthViewModel


@Composable
fun AuthScreen2(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel._authState.observeAsState(NavHostViewModel.AuthState.AUTH)



    AuthContent(
        authState = authState,
        navigateToError = { navController.navigate(Destination.AuthScreenError.route) },
        navigateToSuccess = { navController.navigate(Destination.ScreenEdit.route) },
        auth = viewModel::autorisation,
        login0 = viewModel.getLogin(),
        password0 = viewModel.getPassword(),
        IpServer0 = viewModel.getIpServer(),
        IdDoor0 = viewModel.getIdDoor()
    )

    if (authState == NavHostViewModel.AuthState.SUCCESS){
        viewModel.upload()
    }
}


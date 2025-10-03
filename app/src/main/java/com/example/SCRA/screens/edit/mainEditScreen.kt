package com.example.SCRA.screens.edit

import AuthContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.SCRA.Destination
import com.example.SCRA.NavHostViewModel


@Composable
fun mainEditScreen(
    navController: NavHostController,
    viewModel: EditViewModel = hiltViewModel()
) {
    val dataByQrCode by viewModel.dataByQrCode.observeAsState()
    mainEditContent(
        dataByQrCode = dataByQrCode,
        getDataByQrCode = viewModel::getDataByQrCode
    )
//    val authState by viewModel._authState.observeAsState(NavHostViewModel.AuthState.AUTH)
//
//
//
//    AuthContent(
//        authState = authState,
//        navigateToError = { navController.navigate(Destination.AuthScreenError.route) },
//        navigateToSuccess = { navController.navigate(Destination.ScreenMainJob.route) },
//        auth = viewModel::autorisation,
//        login0 = viewModel.getLogin(),
//        password0 = viewModel.getPassword()
//    )
//
//    if (authState == NavHostViewModel.AuthState.SUCCESS){
//        viewModel.upload()
//    }
}


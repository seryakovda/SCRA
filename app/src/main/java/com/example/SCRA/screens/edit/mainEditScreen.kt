package com.example.SCRA.screens.edit

import AuthContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController


@Composable
fun mainEditScreen(
    navController: NavHostController,
    viewModel: EditViewModel = hiltViewModel()
) {
    val dataByQrCode by viewModel.dataByQrCode.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.startListeningUsbCodes()  // один раз при создании
    }

    mainEditContent(
        dataByQrCode = dataByQrCode,
        getDataByQrCode = viewModel::getDataByQrCode,
        setStateInOut = viewModel::setStateInOut
    )
}

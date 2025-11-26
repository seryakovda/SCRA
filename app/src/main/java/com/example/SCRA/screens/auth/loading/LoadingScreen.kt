package com.example.tire.screens.auth.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.SCRA.NavHostViewModel

import LoadingContent
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.example.SCRA.screens.auth.loading.LoadingState
import com.example.SCRA.screens.auth.loading.LoadingViewModel


@Composable
fun LoadingScreen1(
    navigateToEdit: () -> Unit = { },
    navigateToAuth: () -> Unit = { },
    viewModel: LoadingViewModel = hiltViewModel()
) {
//    val authState by viewModel._authState.observeAsState(NavHostViewModel.AuthState.LOADING)

    LoadingContent()
    val state by viewModel.loadingState.observeAsState()
    Log.i("testConnection", "LoadingScreen1 state: $state")

    LaunchedEffect(state) {
        when(state) {
            is LoadingState.Sucsess -> navigateToEdit()
            is LoadingState.Fail -> navigateToAuth()
            is LoadingState.Loading -> viewModel.testConnection()
            else -> { }
        }
    }

//    LaunchedEffect(null) {
//        Log.i("testConnection", "LoadingScreen2")
//
//        viewModel.testConnection()
//    }

}


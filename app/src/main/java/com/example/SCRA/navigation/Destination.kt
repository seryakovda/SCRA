package com.example.SCRA.navigation

sealed class Destination(val route: String) {
    object AuthScreen: Destination("AuthScreen")
    object AuthScreenError: Destination("AuthScreenError")
    object ScreenMainJob: Destination("ScreenMainJob")
    object ScreenEdit: Destination("ScreenEdit")
}
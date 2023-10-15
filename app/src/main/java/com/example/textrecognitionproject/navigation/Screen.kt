package com.example.textrecognitionproject.navigation

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object MLKitProcess: Screen("ml_kit_process")
}

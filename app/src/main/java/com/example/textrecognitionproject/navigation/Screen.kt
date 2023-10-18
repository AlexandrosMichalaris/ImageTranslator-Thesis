package com.example.textrecognitionproject.navigation

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
}

package com.example.textrecognitionproject.navigation

import AppContentViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "") {
        navigation(
            startDestination = "main",
            route = ""
        ) {
            composable(route = "main") {
                val viewModel = it.sharedViewModel<AppContentViewModel>(navController)
                navController.navigate(route = "mlkitProcess")
            }
            composable(route = "mlkitProcess") {
                val viewModel = it.sharedViewModel<AppContentViewModel>(navController)
            }
        }

    }
}

@Composable
inline fun<reified T: ViewModel>NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
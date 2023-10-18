package com.example.textrecognitionproject.navigation

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
import com.example.textrecognitionproject.AppContentViewModel
import com.example.textrecognitionproject.mainViews.AppContent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "app") {
        navigation(
            startDestination = Screen.MainScreen.route,
            route = "app"
        ) {
            composable(route = Screen.MainScreen.route) {
                val viewModel = it.sharedViewModel<AppContentViewModel>(navController)
                AppContent(viewModel = viewModel)
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
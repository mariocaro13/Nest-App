package com.example.carolsnest.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carolsnest.screens.signup.SignUpScreen
import com.example.carolsnest.screens.bird.BirdDetailScreen
import com.example.carolsnest.screens.profile.ProfileScreen
import com.example.carolsnest.screens.home.HomeScreen
import com.example.carolsnest.screens.login.LoginScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth: FirebaseAuth = Firebase.auth

    var isAuthChecked by remember { mutableStateOf(false) }
    var startDestination by remember { mutableStateOf("login") }
    LaunchedEffect(key1 = Unit) {
        startDestination = if (auth.currentUser != null) {
            "home"
        } else {
            "login"
        }
        isAuthChecked = true
    }
    if (isAuthChecked) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(AppDestinations.HOME_SCREEN) { HomeScreen(navController = navController) }
            composable(AppDestinations.LOGIN_SCREEN) { LoginScreen(navController = navController) }
            composable(AppDestinations.SIGNUP_SCREEN) { SignUpScreen(navController = navController) }
            composable(AppDestinations.LOGIN_SCREEN) { LoginScreen(navController = navController) }
            composable(AppDestinations.PROFILE_SCREEN) { ProfileScreen(navController = navController) }

            composable(
                route = AppDestinations.BIRD_DETAIL_ROUTE,
                arguments = listOf(navArgument(AppDestinations.BIRD_ID_ARG){
                    type = NavType.StringType
                })
            ){
                BirdDetailScreen(navController = navController)
            }
        }
    } else {
        CircularProgressIndicator()
    }
}


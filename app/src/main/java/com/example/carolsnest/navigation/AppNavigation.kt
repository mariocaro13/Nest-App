package com.example.carolsnest.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.carolsnest.factory.BirdDetailViewModelFactory
import com.example.carolsnest.model.BirdDetailViewModel
import com.example.carolsnest.model.SessionViewModel
import com.example.carolsnest.screens.bird.BirdDetailScreen
import com.example.carolsnest.screens.home.HomeScreen
import com.example.carolsnest.screens.login.LoginScreen
import com.example.carolsnest.screens.profile.ProfileScreen
import com.example.carolsnest.screens.signup.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(
    navController: NavHostController, sessionVm: SessionViewModel = viewModel()
) {
    val auth: FirebaseAuth = Firebase.auth
    var isAuthChecked by remember { mutableStateOf(false) }

    var startDestination by remember { mutableStateOf("login") }

    LaunchedEffect(Unit) {
        startDestination = if (auth.currentUser != null) {
            AppDestinations.HOME_SCREEN
        } else {
            AppDestinations.LOGIN_SCREEN
        }
        isAuthChecked = true
    }

    if (isAuthChecked) {
        NavHost(
            navController = navController, startDestination = startDestination
        ) {

            // HOME
            composable(AppDestinations.HOME_SCREEN) {
                HomeScreen(
                    navController = navController,
                )
            }

            // LOGIN
            composable(AppDestinations.LOGIN_SCREEN) {
                LoginScreen(
                    navController = navController
                )
            }

            // SIGNUP
            composable(AppDestinations.SIGNUP_SCREEN) {
                SignUpScreen(
                    navController = navController
                )
            }

            // PROFILE
            composable(AppDestinations.PROFILE_SCREEN) {
                ProfileScreen(
                    navController = navController, sessionVm = sessionVm
                )
            }

            // BIRD DETAIL
            composable(
                route = AppDestinations.BIRD_DETAIL_ROUTE, arguments = listOf(
                navArgument(AppDestinations.BIRD_ID_ARG) {
                    type = NavType.StringType
                })) { backStackEntry ->
                val detailVm: BirdDetailViewModel = viewModel(
                    viewModelStoreOwner = backStackEntry, factory = BirdDetailViewModelFactory(
                        firestore = FirebaseFirestore.getInstance()
                    )
                )
                BirdDetailScreen(
                    birdDetailViewModel = detailVm,
                )
            }
        }
    } else {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    }
}


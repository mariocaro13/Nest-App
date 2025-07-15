package com.example.carolsnest

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carolsnest.content.components.AppBottomBar
import com.example.carolsnest.content.components.BottomTab
import com.example.carolsnest.factory.BirdDetailViewModelFactory
import com.example.carolsnest.model.BirdDetailViewModel
import com.example.carolsnest.model.HomeViewModel
import com.example.carolsnest.model.SessionViewModel
import com.example.carolsnest.navigation.AppDestinations
import com.example.carolsnest.navigation.AppNavigation
import com.example.carolsnest.ui.theme.CarolsNestTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        setContent {
            CarolsNestTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val sessionVm: SessionViewModel = viewModel()
                val profileImageUrl by sessionVm.profileImageUrl.collectAsState()

                Scaffold(
                    modifier = Modifier.navigationBarsPadding(), bottomBar = {
                    if (currentRoute in listOf(
                            AppDestinations.HOME_SCREEN,
                            AppDestinations.PROFILE_SCREEN,
                            AppDestinations.BIRD_DETAIL_ROUTE
                        )
                    ) {
                        AppBottomBar(
                            currentTab = when (currentRoute) {
                                AppDestinations.HOME_SCREEN -> BottomTab.Home
                                AppDestinations.PROFILE_SCREEN -> BottomTab.Profile
                                else -> null
                            }, profileImageUrl = profileImageUrl, onTabSelected = { tab ->
                                when (tab) {
                                    BottomTab.Home -> navController.navigate(AppDestinations.HOME_SCREEN) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId)
                                    }

                                    BottomTab.Profile -> navController.navigate(AppDestinations.PROFILE_SCREEN) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId)
                                    }
                                }
                            })
                    }
                }, floatingActionButton = {
                    when (currentRoute) {
                        // Home Screen
                        AppDestinations.HOME_SCREEN -> {
                            val homeEntry = remember(navBackStackEntry) {
                                navController.getBackStackEntry(AppDestinations.HOME_SCREEN)
                            }
                            val homeVm: HomeViewModel = viewModel(viewModelStoreOwner = homeEntry)
                            SmallFloatingActionButton(
                                onClick = { homeVm.onOpenAddBirdDialog() },
                                modifier = Modifier
                                    .offset(y = (46).dp)
                                    .clip(CircleShape),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add bird")
                            }
                        }

                        // Bird detail Screen
                        AppDestinations.BIRD_DETAIL_ROUTE -> {
                            val detailEntry = remember(navBackStackEntry) {
                                navController.getBackStackEntry(AppDestinations.BIRD_DETAIL_ROUTE)
                            }
                            val detailBirdVm: BirdDetailViewModel = viewModel(
                                viewModelStoreOwner = detailEntry,
                                factory = BirdDetailViewModelFactory(Firebase.firestore)
                            )
                            SmallFloatingActionButton(
                                onClick = { detailBirdVm.onOpenEditBirdDialog() },
                                modifier = Modifier
                                    .offset(y = 46.dp)
                                    .clip(CircleShape),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit bird")
                            }
                        }
                    }
                }, floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }
}

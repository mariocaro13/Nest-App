package com.example.carolsnest.screens.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carolsnest.content.HomeScreenContent
import com.example.carolsnest.model.HomeViewModel
import com.example.carolsnest.navigation.AppDestinations

@Composable
fun HomeScreen(
    navController: NavController, homeViewModel: HomeViewModel = viewModel()
) {
    // Observe the global state from the HomeViewModel.
    val homeState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher for picking multiple images.
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            uris.forEach { uri ->
                homeViewModel.onImageSelected(uri, context)
            }
        })

    // Show a snackbar if there's an error message.
    LaunchedEffect(homeState.addBirdDialogState.errorMessage) {
        homeState.addBirdDialogState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Pass state and callbacks to the UI content.
    HomeScreenContent(
        homeState = homeState,
        snackbarHostState = snackbarHostState,
        onProfileNav = {
            navController.navigate("profile")
        },
        onBirdItemClick = { bird ->
            Log.d("HomeScreen", "Clicked on bird: ${bird.name} (ID: ${bird.id})")
            if (bird.id.isNotEmpty()) {
                navController.navigate("${AppDestinations.BIRD_DETAIL_BASE}/${bird.id}")
            } else {
                Log.e("HomeScreen", "Attempted to navigate with empty bird ID")
            }
        },
        onOpenAddBirdDialog = { homeViewModel.onOpenAddBirdDialog() },
        onDismissAddBirdDialog = { homeViewModel.onDismissAddBirdDialog() },
        onBirdNameChange = { homeViewModel.onBirdNameChange(it) },
        onBirdDescriptionChange = { homeViewModel.onBirdDescriptionChange(it) },
        onSelectImageClick = {
            pickMultipleMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onImageRemoved = { homeViewModel.onImageRemoved(it) },
        onSaveClick = { homeViewModel.saveBird(context) })
}

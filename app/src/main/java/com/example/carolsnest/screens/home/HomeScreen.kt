package com.example.carolsnest.screens.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carolsnest.content.HomeScreenContent
import com.example.carolsnest.model.HomeViewModel
import com.example.carolsnest.navigation.AppDestinations

@Composable
fun HomeScreen(
    navController: NavController,
) {
    val homeVm: HomeViewModel = viewModel()
    // Observe the global state from the HomeViewModel.
    val homeState by homeVm.homeScreenStateStateFlow.collectAsState()
    val context = LocalContext.current

    // Launcher for picking multiple images.
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            uris.forEach { uri ->
                homeVm.onImageSelected(uri, context)
            }
        })

    // Pass state and callbacks to the UI content.
    HomeScreenContent(
        homeState = homeState,
        onBirdItemClick = { bird ->
            Log.d("HomeScreen", "Clicked on bird: ${bird.name} (ID: ${bird.id})")
            if (bird.id.isNotEmpty()) {
                navController.navigate("${AppDestinations.BIRD_DETAIL_BASE}/${bird.id}")
            } else {
                Log.e("HomeScreen", "Attempted to navigate with empty bird ID")
            }
        },
        onDismissAddBirdDialog = { homeVm.onDismissAddBirdDialog() },
        onBirdNameChange = { homeVm.onBirdNameChange(it) },
        onBirdDescriptionChange = { homeVm.onBirdDescriptionChange(it) },
        onSelectImageClick = {
            pickMultipleMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onImageRemoved = { homeVm.onImageRemoved(it) },
        onSaveClick = { homeVm.saveBird() })
}

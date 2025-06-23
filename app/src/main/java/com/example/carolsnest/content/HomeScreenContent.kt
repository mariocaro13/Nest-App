package com.example.carolsnest.content

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.carolsnest.R
import com.example.carolsnest.content.dialogs.AddBirdDialog
import com.example.carolsnest.content.item.BirdCard
import com.example.carolsnest.data.BirdData
import com.example.carolsnest.state.HomeScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    homeState: HomeScreenState,
    snackbarHostState: SnackbarHostState,
    onProfileNav: () -> Unit,
    onBirdItemClick: (BirdData) -> Unit,
    onOpenAddBirdDialog: () -> Unit,
    onDismissAddBirdDialog: () -> Unit,
    onBirdNameChange: (String) -> Unit,
    onBirdDescriptionChange: (String) -> Unit,
    onSelectImageClick: () -> Unit,
    onImageRemoved: (Uri) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Carol's Nest") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                ),
                actions = {
                    IconButton(onClick = onProfileNav) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_bird),
                            contentDescription = "Open profile",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenAddBirdDialog) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add bird"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when {
                homeState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                homeState.birds.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "¡No hay pollos! ¡Añade alguno con el botón '+'!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        homeState.birds.forEach { bird ->
                            BirdCard(
                                bird = bird,
                                onItemClick = { onBirdItemClick(it) }
                            )
                        }
                    }
                }
            }
        }
    }
    if (homeState.showAddBirdDialog) {
        AddBirdDialog(
            dialogState = homeState.addBirdDialogState,
            selectedImageUris = homeState.selectedImageUrisForPreview,
            onBirdNameChange = onBirdNameChange,
            onBirdDescriptionChange = onBirdDescriptionChange,
            onSelectImageClick = onSelectImageClick,
            onImageRemoved = onImageRemoved,
            onDismissRequest = onDismissAddBirdDialog,
            onSaveClick = onSaveClick
        )
    }
}

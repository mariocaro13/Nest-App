package com.example.carolsnest.content

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.carolsnest.content.dialogs.AddBirdDialog
import com.example.carolsnest.content.item.BirdCard
import com.example.carolsnest.data.BirdData
import com.example.carolsnest.state.HomeScreenState

@Composable
fun HomeScreenContent(
    homeState: HomeScreenState,
    onBirdItemClick: (BirdData) -> Unit,
    onDismissAddBirdDialog: () -> Unit,
    onBirdNameChange: (String) -> Unit,
    onBirdDescriptionChange: (String) -> Unit,
    onSelectImageClick: () -> Unit,
    onImageRemoved: (Uri) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            homeState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            homeState.birds.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    homeState.birds.forEach { bird ->
                        BirdCard(bird = bird, onItemClick = { onBirdItemClick(it) })
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.padding(8.dp))
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

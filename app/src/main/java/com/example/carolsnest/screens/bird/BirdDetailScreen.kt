package com.example.carolsnest.screens.bird

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.carolsnest.R
import com.example.carolsnest.model.BirdDetailViewModel

@Composable
fun BirdDetailScreen(
    birdDetailViewModel: BirdDetailViewModel,
) {
    val birdData by birdDetailViewModel.birdData.collectAsState()
    val isLoading by birdDetailViewModel.isLoading.collectAsState()
    val errorMessage by birdDetailViewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (birdData != null) {
            val bird = birdData!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!bird.description.isNullOrBlank()) {
                    item {
                        Text(
                            text = "Descripción:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = bird.description!!,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 22.sp
                        )
                    }
                }

                if (bird.imageUrls.isNotEmpty()) {
                    item {
                        Text(
                            text = "Fotos:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(items = bird.imageUrls, key = { it }) { imageUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                                .crossfade(true).placeholder(R.drawable.placeholder_bird)
                                .error(R.drawable.placeholder_bird).build(),
                            contentDescription = "Foto de ${bird.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "No hay fotos para este pollo. 😭",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Text(
                "Selecciona un pajaro para ver sus detalles..",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

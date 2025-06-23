package com.example.carolsnest.screens.bird

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.carolsnest.R
import com.example.carolsnest.factory.BirdDetailViewModelFactory
import com.example.carolsnest.model.BirdDetailViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdDetailScreen(
    navController: NavController,
    birdDetailViewModel: BirdDetailViewModel = viewModel(
        factory = BirdDetailViewModelFactory(Firebase.firestore)
    )
) {
    val birdData by birdDetailViewModel.birdData.collectAsState()
    val isLoading by birdDetailViewModel.isLoading.collectAsState()
    val errorMessage by birdDetailViewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(birdData?.name ?: "Detalles del Pájaro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = bird.name,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.placeholder_bird)
                                    .error(R.drawable.placeholder_bird)
                                    .build(),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BirdDetailScreenPreview() {
    MaterialTheme {
        val previewBird = com.example.carolsnest.data.BirdData(
            id = "preview1",
            name = "Pájaro de Muestra",
            description = "Esta es una descripción muy detallada de un pájaro de muestra para que se vea bien en la previsualización. Queremos ver cómo se ajusta el texto y si los espaciados son correctos.",
            imageUrls = listOf("url1_placeholder", "url2_placeholder"),
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(previewBird.name) },
                    navigationIcon = {
                        IconButton(onClick = { /* No action */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = previewBird.name,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (!previewBird.description.isNullOrBlank()) {
                        item {
                            Text(
                                text = "Descripción:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = previewBird.description!!,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    if (previewBird.imageUrls.isNotEmpty()) {
                        item {
                            Text(
                                text = "Fotos:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(previewBird.imageUrls) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(bottom = 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.placeholder_bird),
                                    contentDescription = "Imagen Placeholder",
                                    modifier = Modifier.size(100.dp),
                                    tint = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
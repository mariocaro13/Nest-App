package com.example.carolsnest.content.item

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.carolsnest.R
import com.example.carolsnest.content.components.TitleBoxWithDirectionalShadow
import com.example.carolsnest.data.BirdData

/**
 * BirdCard composable displays concise information about a bird.
 */
@Composable
fun BirdCard(
    bird: BirdData, modifier: Modifier = Modifier, onItemClick: (BirdData) -> Unit = {}
) {
    // Basic logging, useful for debugging.
    Log.d("BirdCard", "BirdCard imageURL: ${bird.imageUrls.firstOrNull()}")

    Card(
        onClick = { onItemClick(bird) },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            // Bird name text.
            TitleBoxWithDirectionalShadow(
                title = bird.name,
                shadowOffsetX = 2.dp,
                shadowOffsetY = 2.dp,
            )

            // Display bird image if available.
            val imageUrl = bird.imageUrls.firstOrNull()
            if (imageUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                        .crossfade(true).placeholder(R.drawable.placeholder_bird)
                        .error(R.drawable.placeholder_bird).build(),
                    contentDescription = "Image of ${bird.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            }

            // Bird description text if available.
            bird.description?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

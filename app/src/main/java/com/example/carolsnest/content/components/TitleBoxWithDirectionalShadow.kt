package com.example.carolsnest.content.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TitleBoxWithDirectionalShadow displays a title with a custom directional shadow.
 *
 * @param title The text to display.
 * @param modifier A modifier for this composable.
 * @param shadowOffsetX Horizontal offset for the shadow (default is 4.dp).
 * @param shadowOffsetY Vertical offset for the shadow (default is 4.dp).
 * @param shadowColor The color of the shadow (default is black with 30% opacity).
 */
@Composable
fun TitleBoxWithDirectionalShadow(
    title: String,
    modifier: Modifier = Modifier,
    shadowOffsetX: Dp = 4.dp,
    shadowOffsetY: Dp = 4.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.3f)
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Shadow Box: displaced by the specified offsets.
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffsetX, y = shadowOffsetY)
                .clip(MaterialTheme.shapes.medium)
                .background(shadowColor)
        )
        // Main title Box: placed above the shadow.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

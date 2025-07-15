package com.example.carolsnest.content.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

sealed class BottomTab {
    data object Home    : BottomTab()
    data object Profile : BottomTab()
}

@Composable
fun AppBottomBar(
    currentTab: BottomTab?,
    profileImageUrl: String?,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
                clip = true
            },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // HOME BUTTON
            IconButton(
                onClick = { onTabSelected(BottomTab.Home) }, modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .border(
                            width = 2.dp,
                            color = if (currentTab == BottomTab.Home) Color.White
                            else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.background)
            )

            // PROFILE BUTTON
            IconButton(
                onClick = { onTabSelected(BottomTab.Profile) }, modifier = Modifier.weight(1f)
            ) {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile pic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (currentTab == BottomTab.Profile) Color.White
                                else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = if (currentTab == BottomTab.Profile) Color.White
                        else Color.Black,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (currentTab == BottomTab.Home) Color.White
                                else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}
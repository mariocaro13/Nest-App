package com.example.carolsnest.content.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtonsRow(
    isLoading: Boolean,
    firstButtonText: String,
    secondButtonText: String,
    onFirstButtonClick: () -> Unit,
    onSecondButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onFirstButtonClick) {
                Text(text = firstButtonText)
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = onSecondButtonClick) {
                Text(text = secondButtonText)
            }
        }
    }
}
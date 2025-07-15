package com.example.carolsnest.content.dialogs

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.carolsnest.state.AddBirdDialogState

@Composable
fun EditBirdDialog(
    dialogState: AddBirdDialogState,
    selectedImageUris: List<Uri>,
    onBirdDescriptionChange: (String) -> Unit,
    onSelectImageClick: () -> Unit,
    onImageRemoved: (Uri) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!dialogState.isUploading) onDismissRequest() },
        title = { Text("Editar Pollo") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                OutlinedTextField(
                    value = dialogState.birdDescription,
                    onValueChange = onBirdDescriptionChange,
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !dialogState.isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSelectImageClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !dialogState.isUploading
                ) {
                    Text("Añadir mas Foticos")
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedImageUris.isNotEmpty()) {
                    Text("Fotos seleccionadas:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = selectedImageUris, key = { it.toString() }) { uri ->
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(model = uri),
                                    contentDescription = "Imagen seleccionada",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                if (!dialogState.isUploading) {
                                    IconButton(
                                        onClick = { onImageRemoved(uri) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(0.dp)
                                            .background(
                                                MaterialTheme.colorScheme.error.copy(
                                                    alpha = 0.5f
                                                ), CircleShape
                                            )
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Quitar imagen",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }


                if (dialogState.isUploading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(
                        "Actualizando pollo...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }

                dialogState.errorMessage?.let {
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                onClick = onSaveClick,
                enabled = !dialogState.isUploading && dialogState.birdName.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                onClick = { if (!dialogState.isUploading) onDismissRequest() },
                enabled = !dialogState.isUploading,
            ) {
                Text(
                    "Cancelar"
                )
            }
        }
    )
}
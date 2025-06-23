package com.example.carolsnest.imgbb

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.carolsnest.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.IOException

suspend fun uploadImageToImgBB(
    context: Context,
    imageUri: Uri,
    maxSizeKb: Int = 1024
): String? {
    val apiKeyFromBuildConfig = BuildConfig.IMGBB_API_KEY

    if (apiKeyFromBuildConfig.isEmpty()) {
        return null
    }

    return withContext(Dispatchers.IO) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }

        try {
            val imageBytes = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                var quality = 90
                var stream = ByteArrayOutputStream()
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

                while (stream.size() / 1024 > maxSizeKb && quality > 10) {
                    stream.reset()
                    quality -= 10
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                }
                stream.toByteArray()
            }

            if (imageBytes == null) {
                client.close()
                return@withContext null
            }

            val mimeType = "image/jpeg"
            val fileName = getFileNameFromUri(context, imageUri).let {
                if (it.endsWith(".jpeg") || it.endsWith(".jpg")) it else "$it.jpg"
            }

            val response: ImgBbResponse = client.post("https://api.imgbb.com/1/upload") {
                parameter("key", apiKeyFromBuildConfig)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                ))
            }.body()
            Log.d("ImgBBUploadKtor", "Respuesta de ImgBB: $response")
            client.close()

            if (response.success && response.data != null) {
                return@withContext response.data.url
            } else {
                val errorMessage = response.error?.message
                val errorCode = response.error?.code
                Log.e("ImgBBUploadKtor", "Error: $errorCode: $errorMessage")
                return@withContext null
            }

        } catch (_: IOException) {
            client.close()
            return@withContext null
        }
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName: String? = null
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("FileNameUtil", "Error: ", e)
        }
    }
    if (fileName == null) {
        val pathFromUri: String? = uri.path
        val path = pathFromUri ?: ""
        val cut = path.lastIndexOf('/')
        if (cut != -1) {
            if (pathFromUri != null) {
                fileName = pathFromUri.substring(cut + 1)
            }
        }
    }
    return fileName ?: "uploaded_image_${System.currentTimeMillis()}"
}
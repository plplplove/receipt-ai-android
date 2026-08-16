package com.receiptai.tracker.data.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.receiptai.tracker.BuildConfig
import com.receiptai.tracker.domain.model.ScannedReceipt
import com.receiptai.tracker.domain.repository.ReceiptParser
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class NvidiaReceiptParser @Inject constructor() : ReceiptParser {

    private val client = OkHttpClient.Builder()
        .callTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    override suspend fun parseReceipt(imageBytes: ByteArray): ScannedReceipt? =
        withContext(Dispatchers.IO) {
            if (BuildConfig.NVIDIA_API_KEY.isBlank()) return@withContext null
            val jpegBytes = recompressForUpload(imageBytes)
            val dataUrl = "data:image/jpeg;base64," +
                Base64.encodeToString(jpegBytes, Base64.NO_WRAP)

            val payload = buildJsonObject {
                put("model", MODEL_ID)
                put("max_tokens", 2048)
                put("temperature", 0.2)
                put("top_p", 0.95)
                put("stream", false)
                put("chat_template_kwargs", buildJsonObject { put("enable_thinking", false) })
                put(
                    "messages",
                    buildJsonArray {
                        add(
                            buildJsonObject {
                                put("role", "user")
                                put(
                                    "content",
                                    buildJsonArray {
                                        add(
                                            buildJsonObject {
                                                put("type", "image_url")
                                                put(
                                                    "image_url",
                                                    buildJsonObject { put("url", dataUrl) }
                                                )
                                            }
                                        )
                                        add(
                                            buildJsonObject {
                                                put("type", "text")
                                                put("text", ScannedReceiptJson.EXTRACTION_PROMPT)
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }

            val request = Request.Builder()
                .url(ENDPOINT)
                .header("Authorization", "Bearer ${BuildConfig.NVIDIA_API_KEY}")
                .header("Accept", "application/json")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string().orEmpty()
                    if (!response.isSuccessful) return@withContext null
                    val content = ScannedReceiptJson.json.parseToJsonElement(body)
                        .jsonObject["choices"]?.jsonArray
                        ?.firstOrNull()?.jsonObject
                        ?.get("message")?.jsonObject
                        ?.get("content")?.jsonPrimitive?.content
                        ?.trim().orEmpty()
                    if (content.isEmpty()) return@withContext null
                    ScannedReceiptJson.parseScannedReceipt(content)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                null
            }
        }

    private fun recompressForUpload(imageBytes: ByteArray): ByteArray {
        val bitmap = try {
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (throwable: Throwable) {
            return imageBytes
        } ?: return imageBytes
        val longestSide = max(bitmap.width, bitmap.height)
        val scaled = if (longestSide > MAX_IMAGE_SIDE) {
            val scale = MAX_IMAGE_SIDE.toFloat() / longestSide
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt().coerceAtLeast(1),
                (bitmap.height * scale).toInt().coerceAtLeast(1),
                true
            )
        } else {
            bitmap
        }
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        return output.toByteArray()
    }

    private companion object {
        const val ENDPOINT = "https://integrate.api.nvidia.com/v1/chat/completions"
        const val MODEL_ID = "nvidia/nemotron-3-nano-omni-30b-a3b-reasoning"
        const val REQUEST_TIMEOUT_SECONDS = 30L
        const val MAX_IMAGE_SIDE = 1600
        const val JPEG_QUALITY = 85
    }
}

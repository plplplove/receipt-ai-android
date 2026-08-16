package com.receiptai.tracker.data.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content
import com.receiptai.tracker.BuildConfig
import com.receiptai.tracker.domain.model.ScannedReceipt
import com.receiptai.tracker.domain.repository.ReceiptParser
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Singleton
class GeminiReceiptParser @Inject constructor() : ReceiptParser {

    override suspend fun parseReceipt(imageBytes: ByteArray): ScannedReceipt? {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) return null
        val bitmap = withContext(Dispatchers.IO) {
            val decodedBitmap = try {
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (throwable: Throwable) {
                null
            } ?: return@withContext null
            downscale(decodedBitmap)
        } ?: return null

        val requestContent = content {
            image(bitmap)
            text(ScannedReceiptJson.EXTRACTION_PROMPT)
        }

        for (modelName in CandidateModels) {
            val model = GenerativeModel(
                modelName = modelName,
                apiKey = BuildConfig.GEMINI_API_KEY,
                requestOptions = RequestOptions(timeout = REQUEST_TIMEOUT_SECONDS.seconds)
            )
            var attempt = 1
            while (attempt <= MAX_ATTEMPTS_PER_MODEL) {
                try {
                    val responseText = model.generateContent(requestContent)
                        .text?.trim().orEmpty()
                    if (responseText.isEmpty()) break
                    return ScannedReceiptJson.parseScannedReceipt(responseText)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    if (attempt < MAX_ATTEMPTS_PER_MODEL) {
                        delay(RETRY_BACKOFF_BASE_MS * attempt)
                    }
                }
                attempt += 1
            }
        }
        return null
    }

    private fun downscale(bitmap: Bitmap): Bitmap {
        val longestSide = max(bitmap.width, bitmap.height)
        if (longestSide <= MAX_IMAGE_SIDE) return bitmap
        val scale = MAX_IMAGE_SIDE.toFloat() / longestSide
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt().coerceAtLeast(1),
            (bitmap.height * scale).toInt().coerceAtLeast(1),
            true
        )
    }

    private companion object {
        val CandidateModels = listOf(
            "gemini-flash-lite-latest",
            "gemini-flash-latest",
            "gemini-3.1-flash-lite"
        )
        const val MAX_ATTEMPTS_PER_MODEL = 2
        const val RETRY_BACKOFF_BASE_MS = 800L
        const val REQUEST_TIMEOUT_SECONDS = 20L
        const val MAX_IMAGE_SIDE = 1600
    }
}

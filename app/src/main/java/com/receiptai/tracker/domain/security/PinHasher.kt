package com.receiptai.tracker.domain.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

class PinHasher @Inject constructor() {

    fun hash(pin: String): String {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { secureRandom.nextBytes(it) }
        return salt.toHex() + SEPARATOR + digest(pin, salt).toHex()
    }

    fun verify(pin: String, stored: String): Boolean {
        val parts = stored.split(SEPARATOR)
        if (parts.size != 2) return false
        val salt = runCatching { parts[0].hexToBytes() }.getOrNull() ?: return false
        val expected = runCatching { parts[1].hexToBytes() }.getOrNull() ?: return false
        return digest(pin, salt).contentEquals(expected)
    }

    private fun digest(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    private companion object {
        const val ALGORITHM = "PBKDF2WithHmacSHA256"
        const val ITERATIONS = 60_000
        const val KEY_LENGTH_BITS = 256
        const val SALT_LENGTH_BYTES = 16
        const val SEPARATOR = ":"
        val secureRandom = SecureRandom()

        fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

        fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}

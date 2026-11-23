package com.example.data.local

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
  private const val ALGORITHM = "PBKDF2WithHmacSHA256"
  private const val ITERATIONS = 120_000
  private const val KEY_LENGTH_BITS = 256
  private const val SALT_BYTES = 16

  private val secureRandom = SecureRandom()

  data class HashedPassword(val hashHex: String, val saltHex: String)

  fun hash(password: String): HashedPassword {
    val salt = ByteArray(SALT_BYTES)
    secureRandom.nextBytes(salt)
    val hash = deriveKey(password.toCharArray(), salt)
    return HashedPassword(hashHex = hash.toHexString(), saltHex = salt.toHexString())
  }

  fun verify(password: String, expectedHashHex: String, saltHex: String): Boolean {
    val salt = saltHex.hexToByteArrayOrNull() ?: return false
    val expectedHash = expectedHashHex.hexToByteArrayOrNull() ?: return false
    val actualHash = deriveKey(password.toCharArray(), salt)
    val isMatch = MessageDigest.isEqual(actualHash, expectedHash)
    actualHash.fill(0)
    return isMatch
  }

  private fun deriveKey(passwordChars: CharArray, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(passwordChars, salt, ITERATIONS, KEY_LENGTH_BITS)
    return try {
      SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    } finally {
      spec.clearPassword()
      passwordChars.fill('\u0000')
    }
  }

  private fun ByteArray.toHexString(): String = joinToString(separator = "") { "%02x".format(it) }

  private fun String.hexToByteArrayOrNull(): ByteArray? {
    if (length % 2 != 0) return null
    return try {
      ByteArray(length / 2) { index ->
        substring(index * 2, index * 2 + 2).toInt(16).toByte()
      }
    } catch (_: NumberFormatException) {
      null
    }
  }
}

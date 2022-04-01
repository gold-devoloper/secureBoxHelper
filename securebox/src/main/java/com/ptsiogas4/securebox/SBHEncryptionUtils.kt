package com.ptsiogas4.securebox

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SBHEncryptionUtils {
    companion object {
        @Deprecated(
            "This method is only used for migration reasons. It will be totally removed in the coming versions",
            ReplaceWith(
                "getCipherV2()"
            )
        )
        private fun getCipher(): Cipher {
            return Cipher.getInstance("AES/CBC/PKCS7Padding")
        }

        private fun getCipherV2(): Cipher {
            return Cipher.getInstance("AES/GCM/NoPadding")
        }

        private fun getCipherArray_migration(
            inputArray: ByteArray?, optMode: Int, keySpec: SecretKeySpec,
            ivSpec: IvParameterSpec
        ): ByteArray {
            val cipher = getCipher()
            cipher.init(optMode, keySpec, ivSpec)
            return cipher.doFinal(inputArray)
        }

        private fun getCipherArray(
            inputArray: ByteArray?, optMode: Int, keySpec: SecretKeySpec,
            ivSpec: IvParameterSpec
        ): ByteArray {
            val cipher = getCipherV2()
            cipher.init(optMode, keySpec, ivSpec)
            return cipher.doFinal(inputArray)
        }

        private fun getSecretKeyFactory(): SecretKeyFactory {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        }

        @Deprecated(
            "This method is only used for migration reasons. It will be totally removed in the coming versions",
            ReplaceWith(
                "getSecretKeyFactory()"
            )
        )
        private fun getSecretKeyFactory_migration(): SecretKeyFactory {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        }

        fun getRandomByteArray(arraySize: Int): ByteArray {
            val random = SecureRandom()
            val salt = ByteArray(arraySize)
            random.nextBytes(salt)
            return salt
        }

        fun encryptByteArray(
            inputArray: ByteArray,
            keySpec: SecretKeySpec,
            ivSpec: IvParameterSpec
        ): ByteArray {
            return getCipherArray(
                inputArray = inputArray,
                optMode = Cipher.ENCRYPT_MODE,
                keySpec = keySpec,
                ivSpec = ivSpec
            )
        }

        fun decryptByteArray(
            inputArray: ByteArray?,
            keySpec: SecretKeySpec,
            ivSpec: IvParameterSpec
        ): ByteArray {
            return getCipherArray(
                inputArray = inputArray,
                optMode = Cipher.DECRYPT_MODE,
                keySpec = keySpec,
                ivSpec = ivSpec
            )
        }

        fun decryptByteArray_migration(
            inputArray: ByteArray?,
            keySpec: SecretKeySpec,
            ivSpec: IvParameterSpec
        ): ByteArray {
            return getCipherArray_migration(
                inputArray = inputArray,
                optMode = Cipher.DECRYPT_MODE,
                keySpec = keySpec,
                ivSpec = ivSpec
            )
        }

        fun getKeySpec(
            passwordString: String,
            salt: ByteArray?,
            useMigrationFactory: Boolean = false
        ): SecretKeySpec {
            val passwordChar = passwordString.toCharArray() //Turn password into char[] array
            val pbKeySpec = PBEKeySpec(passwordChar, salt, 1324, 256) //1324 iterations
            if (useMigrationFactory) {
                val keyBytes = getSecretKeyFactory_migration().generateSecret(pbKeySpec).encoded
                return SecretKeySpec(keyBytes, "AES/CFB/PKCS5Padding")
            } else {
                val keyBytes = getSecretKeyFactory().generateSecret(pbKeySpec).encoded
                return SecretKeySpec(keyBytes, "AES/CFB/PKCS5Padding")
            }
        }

        @SuppressLint("HardwareIds")
        fun getSecureId(context: Context?): String {
            val secureAndroidId = Settings.Secure.getString(
                context?.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            var androidId = "UNKNOWN"
            if (secureAndroidId.isNotEmpty()) {
                androidId = secureAndroidId
            }
            return androidId
        }
    }
}
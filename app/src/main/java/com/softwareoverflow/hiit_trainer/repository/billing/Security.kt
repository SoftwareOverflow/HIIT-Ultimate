/**
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwareoverflow.hiit_trainer.repository.billing

import android.text.TextUtils
import android.util.Base64
import com.softwareoverflow.hiit_trainer.BuildConfig
import timber.log.Timber
import java.io.IOException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

/**
 * This class is an addendum. It shouldn't really be here: it should be on the secure server. But if
 * a secure server does not exist, it's still good to check the signature of the purchases coming
 * from Google Play. At the very least, it will combat man-in-the-middle attacks. Putting it
 * on the server would provide the additional protection against hackers who may
 * decompile/rebuild the app.
 *
 * Sigh... All sorts of attacks can befall your app, website, or platform. So when it comes to
 * implementing security measures, you have to be realistic and judicious so that user experience
 * does not suffer needlessly. And you should analyze that the money you will save (minus cost of labor)
 * by implementing security measure X is greater than the money you would lose if you don't
 * implement X. Talk to a UX designer if you find yourself obsessing over security.
 *
 * The good news is, in implementing [BillingRepository], a number of measures is taken to help
 * prevent fraudulent activities in your app. We don't just focus on tech savvy hackers, but also
 * on fraudulent users who may want to exploit loopholes. Just to name an obvious case:
 * triangulation using Google Play, your secure server, and a local cache helps against non-techie
 * frauds.
 */

/**
 * Security-related methods. For a secure implementation, all of this code should be implemented on
 * a server that communicates with the application on the device.
 */
object Security {
    private const val KEY_FACTORY_ALGORITHM = "RSA"
    private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

    /**
     * BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console, usually under Services & APIs tab).
     * This is not your developer public key, it's the *app-specific* public key.
     *
     * Just like everything else in this class, this public key should be kept on your server.
     * But if you don't have a server, then you should obfuscate your app so that hackers cannot
     * get it. If you cannot afford a sophisticated obfuscator, instead of just storing the entire
     * literal string here embedded in the program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private fun getBase64EncodedPublicKey() : String{
        val parts = mutableListOf<String>()

        val split = BuildConfig.IAB_PUBLIC_KEY.split(" ")
        for(i in 0 until split.count() step 2){

            val reversed = reverseCase(split[i + 1])

            val b = reversed.map {
                it.toInt() xor split[i].toInt()
            }

            val c = b.map {
                it.toChar()
            }

            val d = c.joinToString(separator = "")

            parts.add(d)
        }

        return parts.joinToString(separator = "")
    }

    private fun reverseCase(s: String): String {
        return s.map {
            if(it.isUpperCase()){
                it.toLowerCase()
            } else
                it.toUpperCase()
        }.joinToString(separator = "")
    }


    /**
     * Verifies that the data was signed with the given signature
     *
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    fun verifyPurchase(signedData: String, signature: String): Boolean {
        val base64PublicKey = getBase64EncodedPublicKey()
        if ((TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey)
                    || TextUtils.isEmpty(signature))
        ) {
            Timber.w("Purchase verification failed: missing data.")
            return false
        }
        val key = generatePublicKey(base64PublicKey)
        return verify(key, signedData, signature)
    }

    /**
     * Generates a PublicKey instance from a string containing the Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            Timber.w(msg)
            throw IOException(msg)
        }
    }

    /**
     * Verifies that the signature from the server matches the computed signature on the data.
     * Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Timber.w("Base64 decoding failed.")
            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Timber.w("Signature verification failed...")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            Timber.w("Invalid key specification.")
        } catch (e: SignatureException) {
            Timber.w("Signature exception.")
        }
        return false
    }
}
package com.cybersource.flex.Authorization.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

class PayloadUtility() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSignedSignature(signatureStr: String, merchantSecretKey: String): String {
        var secretKey = SecretKeySpec(Base64.getDecoder().decode(merchantSecretKey), "HmacSHA256")
        var aKeyId = Mac.getInstance("HmacSHA256")
        aKeyId.init(secretKey)
        aKeyId.update(signatureStr.toByteArray())
        val aHeaders = aKeyId.doFinal()
        val signedMessage = Base64.getEncoder().encodeToString(aHeaders)

        return signedMessage
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDigest(messageBody: String): String? {
        var digestString = MessageDigest.getInstance("SHA-256")
        val digestBytes = digestString.digest(messageBody.toByteArray(UTF_8))

        var blueprint = Base64.getEncoder().encodeToString(digestBytes)
        blueprint = "SHA-256" + "=" + blueprint
        return blueprint
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNewDate(): String {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")))
    }
}
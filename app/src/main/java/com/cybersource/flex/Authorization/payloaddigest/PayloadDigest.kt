package com.cybersource.flex.Authorization.payloaddigest

import com.cybersource.flex.Authorization.core.MerchantConfig
import com.cybersource.flex.Authorization.util.PayloadUtility

public class PayloadDigest(merchantConfig: MerchantConfig) {
    private var blueprint: String? = null
    private var messageBody: String? = null
    private var merchantConfig: MerchantConfig

    init {
        this.merchantConfig = merchantConfig
    }


    private fun payloadGeneration(): String? {
        messageBody = this.merchantConfig.requestData
        return messageBody
    }

    private fun digestGeneration(): String? {
        /*
         * This method return Digest value which is SHA-256 hash of payload that
         * is BASE64 encoded
         */

        var messageBody = payloadGeneration()
        var digestString = PayloadUtility().getDigest(messageBody!!)

        return digestString
    }

    fun getDigest(): String? {
        return digestGeneration()
    }
}
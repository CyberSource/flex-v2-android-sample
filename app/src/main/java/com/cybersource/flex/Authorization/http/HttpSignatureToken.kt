package com.cybersource.flex.Authorization.http

import com.cybersource.flex.Authorization.core.MerchantConfig
import com.cybersource.flex.Authorization.util.Constants

/* HttpSigToken return SignatureHeader Value that contains following paramters
 * keyid     -- Merchant ID obtained from EBC portal
 * algorithm -- Should have value as "HmacSHA256"
 * headers   -- List of all header name passed in the Signature paramter 
                Note: Digest is not passed for GET calls
 * signature -- Signature header has paramter called signature
                Paramter 'Signature' must contain all the paramters mentioned in header above in given order  */

class HttpSignatureToken(merchantConfig: MerchantConfig) {

    private var merchantConfigSigHead: MerchantConfig
    private var signatureHeader: StringBuilder = StringBuilder()
    private var merchantkeyId: String? = null
    private var merchantSecertKey: String? = null
    private var httpMethod: String? = null
    private var httpMerchantID: String? = null
    private val requestData: String? = null

    init {
        this.merchantConfigSigHead = merchantConfig
        this.merchantkeyId = merchantConfig.merchantKeyId
        this.merchantSecertKey = merchantConfig.merchantSecretKey
        this.httpMethod = merchantConfig.requestType
        this.httpMerchantID = merchantConfig.merchantID
    }

    fun getToken(): String? {
        var signature: String? = null

        try {
            signature = signatureHeader()
        } catch (e: Exception) {
            print(e)
        }

        return signature
    }

    /**
     * @return headers to generate signature.
     * @throws InvalidKeyException
     *             - if key is not valid.
     * @throws NoSuchAlgorithmException
     *             - if algorith is not available.
     */
    private fun signatureHeader(): String {

        /* KeyId is the key obtained from EBC */
        signatureHeader.append("keyid=\"$merchantkeyId\"")
        /* Algorithm should be always HmacSHA256 for http signature */
        signatureHeader.append(", algorithm=\"HmacSHA256\"");

        signatureHeader.append(", headers=\"" + getRequestHeaders(Constants.POST) + "\"")

        /*
        * Get Value for parameter 'Signature' to be passed to Signature Header
        */
        var signatureValue = SignatureGenerator(merchantConfigSigHead).signatureGeneration()
        signatureHeader.append(", signature=\"" + signatureValue + "\"")

        return signatureHeader.toString()
    }

    private fun getRequestHeaders(requestType: String): String {
        var requestHeader: String = ""

        requestHeader = when(requestType) {
            Constants.GET -> "host date (request-target)\" + \" \" + \"v-c-merchant-id"

            Constants.POST -> "host date (request-target) digest v-c-merchant-id"

            else -> ""
        }

        return requestHeader
    }
}
package com.cybersource.flex.Authorization.http

import com.cybersource.flex.Authorization.core.MerchantConfig
import com.cybersource.flex.Authorization.payloaddigest.PayloadDigest
import com.cybersource.flex.Authorization.util.Constants
import com.cybersource.flex.Authorization.util.PayloadUtility

class SignatureGenerator(merchantConfig: MerchantConfig) {

    private var merchantConfig: MerchantConfig
    private var signatureParameterBase64Encoded: String? = null
    private var httpMethod: String? = null
    private var merchantSecertKey: String? = null
    private var merchantId: String? = null
    //private var portfolioId: String
    //private var useMetaKey: Boolean

    init {
        this.merchantConfig = merchantConfig
        this.httpMethod = merchantConfig.requestType
        this.merchantSecertKey = merchantConfig.merchantSecretKey
        this.merchantId = merchantConfig.merchantID
        //this.useMetaKey = merchantConfig.isUseMetaKeyEnabled()
    }

    fun signatureGeneration(): String? {
        var signatureString = StringBuilder()

        signatureString.append("\n")
        signatureString.append(Constants.HOST.toLowerCase())
        signatureString.append(": ")
        signatureString.append(merchantConfig.requestHost)
        signatureString.append("\n")
        signatureString.append(Constants.DATE.toLowerCase())
        signatureString.append(": ")
        signatureString.append(PayloadUtility().getNewDate())
        signatureString.append("\n")
        signatureString.append("(request-target)")
        signatureString.append(": ")

        var requestTarget: String = getRequestTarget(Constants.POST)
        signatureString.append(requestTarget)

        signatureString.append("\n")

        //TODO: if condition required
        //if (httpMethod === Constants.POST) {
        if(true) {
            signatureString.append(Constants.DIGEST.toLowerCase())
            signatureString.append(": ")
            signatureString.append(PayloadDigest(merchantConfig).getDigest())
            signatureString.append("\n")
        }

        signatureString.append(Constants.V_C_MERCHANTID)
        signatureString.append(": ")
        signatureString.append(merchantId)

        signatureString.deleteRange(0, 1)

        var signatureStr = signatureString.toString()

        signatureParameterBase64Encoded = PayloadUtility().getSignedSignature(signatureStr, merchantSecertKey!!)

        return signatureParameterBase64Encoded
    }

    private fun getRequestTarget(requestType: String): String {
        var requestTarget: String = ""
        when (requestType) {
            //TODO: remove this hardcode
            Constants.POST -> requestTarget = "POST".toLowerCase() + " " + "/flex/v2/sessions";//Constants.POST.toLowerCase() + Constants.SPACE + merchantConfig.requestTarget
        }

        return requestTarget
    }
}
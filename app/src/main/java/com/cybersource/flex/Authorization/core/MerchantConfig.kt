package com.cybersource.flex.Authorization.core

class MerchantConfig {

    var merchantKeyId: String? = null
    var merchantSecretKey: String? = null
    var merchantID: String? = null
    var url: String? = null
    var requestTarget: String? = null
    var authenticationType: String? = null
    var requestHost: String? = null
    var responseMessage: String? = null
    var responseCode: String? = null
    var requestType: String? = null
    var runEnvironment: String? = null
    var requestJsonPath: String? = null
    var requestData: String? = null
    var useMetaKey: Boolean = false

//    fun setRunEnvironment(runEnvironment: String) { this.runEnvironment = runEnvironment }
//
//    fun getMerchantKeyId(): String? { return merchantKeyId }
//    fun setMerchantKeyId(merchantKeyId: String) { this.merchantKeyId = merchantKeyId }
//
//    fun getMerchantSecretKey(): String? { return merchantSecretKey }
//    fun setMerchantSecretKey(merchantsecretKey: String) { this.merchantSecretKey = merchantsecretKey }
//
//    fun getMerchantID(): String? { return merchantID }
//    fun setMerchantID(merchantID: String) { this.merchantID = merchantID }
//
//    fun getUrl(): String? { return url }
//    fun setUrl(url: String) { this.url = url }
//
//    fun getRequestTarget(): String? { return requestTarget }
//    fun setRequestTarget(requestTarget: String) { this.requestTarget = requestTarget }
//
//    fun getAuthenticationType(): String? { return authenticationType }
//    fun setAuthenticationType(authenticationType: String) { this.authenticationType = authenticationType }
//
//    fun getRequestHost(): String? { return requestHost }
//    fun setRequestHost(requestHost: String) { this.requestHost = requestHost }
//
//    fun getResponseMessage(): String? { return responseMessage }
//    fun setResponseMessage(responseMessage: String) { this.responseMessage = responseMessage }
//
//    fun getResponseCode(): String? { return responseCode }
//    fun setResponseCode(responseCode: String) { this.responseCode = responseCode }
//
//    fun getRequestType(): String? { return requestType }
//    fun setRequestType(requestType: String) { this.requestType = requestType }
//
//    fun getRequestJsonPath(): String? { return requestJsonPath }
//    fun setRequestJsonPath(requestJsonPath: String) { this.requestJsonPath = requestJsonPath }
//
//    fun getRequestData(): String? { return requestJsonPath }
//    fun setRequestData(requestData: String) { this.requestData = requestData }

    fun isUseMetaKeyEnabled(): Boolean {
        //TODO
        return false
    }
}
package com.cybersource.flex.Authorization.core

import com.cybersource.flex.Authorization.http.HttpSignatureToken

class Authorization {
    private var jwtRequestBody: String? = null

    fun setJWTRequestBody(jwtrequest: String) {
        jwtRequestBody = jwtrequest
    }

    fun getToken(merchantConfig: MerchantConfig): String {
        return HttpSignatureToken(merchantConfig).getToken()!!
    }
}
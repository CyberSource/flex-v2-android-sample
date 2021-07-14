package com.cybersource.flex.sessions.model

data class FlexCardData(
        var number: FlexFieldData? = null,
        var securityCode: FlexFieldData? = null,
        var expirationMonth: FlexFieldData? = null,
        var expirationYear: FlexFieldData? = null,
        var type: FlexFieldData? = null
)

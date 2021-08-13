
# Cybersource Flex Android - SampleApp  

  
  This SDK allows mobile developers to provide credit card payment functionality within their Android applications, without having to pass sensitive card data back to their application backend servers.  For more information on including payments in your mobile application see our [InApp Payments Guide](TBD)   
     
  ## SDK Installation 

  ### Gradle
  ```
  dependencies {
    implementation "com.cybersource:flex-api-android-client:1.0.0"
  }
 ```  
 Note: Update your Gradle local.properties with your local machines username

 ## SDK Usage

  ### Configure merchant details
  ```Kotlin
      val MERCHANT_ID = "<MerchantID>"
      val MERCHANT_SECRET = "<MerchantSecret>"
      val MERCHANT_KEY = "<MerchantKey>"
  ```

  ### Create capture context
  Please refer sample application which demonstrates creation of Capture context  
  [Sample App](https://github.com/CyberSource/flex-v2-android-sample)

  ```Kotlin
          val captureContext = createCaptureContext()
  ```

  ### Initialize the SDK and create transient token using capture context
  ```Kotlin
        val payloadItems = getPayloadData()
        val cc = CaptureContext.fromJwt(keyId)
        var flexService = FlexService.getInstance()

        try {
            flexService.createTokenAsyncTask(cc, payloadItems, object :
                TransientTokenCreationCallback {
                override fun onSuccess(tokenResponse: TransientToken?) {
                    //handle successful response
                }

                override fun onFailure(error: FlexException?) {
                    //handle failed response
                }
            })
        } catch (e: FlexException) {
            //handle exception
        }
  ```
  ### Create payload
  ```swift
        private fun getPayloadData(): Map<String, Any>? {
            val sad: MutableMap<String, Any> = HashMap()

            sad["paymentInformation.card.number"] = "4111111111111111"
            sad["paymentInformation.card.securityCode"] = "123"
            sad["paymentInformation.card.expirationMonth"] = "12"
            sad["paymentInformation.card.expirationYear"] = "29"

            return sad
        }  
  ```
  ### Using the Accept Payment Token to Create a Transaction Request
  Your server constructs a transaction request using the [Cybersource API](https://developer.cybersource.com/), placing the encrypted payment information that it received in previous step in the opaqueData element.
  ```json
     {
      "createTransactionRequest": {
          "merchantAuthentication": {
              "name": "YOUR_API_LOGIN_ID",
              "transactionKey": "YOUR_TRANSACTION_KEY"
          },
          "refId": "123456",
          "transactionRequest": {
              "transactionType": "authCaptureTransaction",
              "amount": "5",
              "payment": {
                  "opaqueData": {
                      "dataDescriptor": "COMMON.ACCEPT.INAPP.PAYMENT",
                      "dataValue": "PAYMENT_NONCE_GOES_HERE"
                  }
              }
          }
      }
  }
  ```
  ## Sample Application
  We have a sample application which demonstrates the SDK usage:  
  [Sample App](https://github.com/CyberSource/flex-v2-android-sample)

  ## Important note:
  The generation of the capture context should originate from your payment application server.  As this is a fully authenticated REST api it requires your API credentials which are not secured on a mobile application.  It has been included in this demonstration for the purpose of convenience and to demonstrate an end-to-end payment flow.

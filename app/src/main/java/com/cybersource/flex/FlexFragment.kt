package com.cybersource.flex

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.cybersource.flex.Authorization.core.Authorization
import com.cybersource.flex.Authorization.core.MerchantConfig
import com.cybersource.flex.Authorization.payloaddigest.PayloadDigest
import com.cybersource.flex.Authorization.util.Constants
import com.cybersource.flex.Authorization.util.PayloadUtility
import com.cybersource.flex.android.*
import com.cybersource.flex.sessions.api.Environment
import com.cybersource.flex.sessions.api.FlexSessionServiceGenerator
import com.cybersource.flex.sessions.model.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class FlexFragment : Fragment() {

    private lateinit var checkoutButton: Button
    private lateinit var cardNumberView: EditText
    private lateinit var monthView: EditText
    private lateinit var yearView: EditText
    private lateinit var cvvView: EditText

    private lateinit var progressDialog: ProgressDialog
    private lateinit var responseLayout: RelativeLayout
    private lateinit var responseTitle: TextView
    private lateinit var responseValue: TextView

    private var cardNumber: String? = null
    private var month: String? = null
    private var year: String? = null
    private var cvv: String? = null

    private var keyId: String? = null
    private var container: ViewGroup? = null
    private var inflater: LayoutInflater? = null

    companion object {
        val MERCHANT_ID = ""
        val MERCHANT_SECRET = ""
        val MERCHANT_KEY = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.container = container
        this.inflater = inflater

        val view = inflater.inflate(R.layout.fragment_flex, container, false)
        initialize(view)

        return view
    }

    private fun initialize(view: View) {
        cardNumberView = view.findViewById<View>(R.id.card_number_view) as EditText
        setUpCreditCardEditText()
        monthView = view.findViewById<View>(R.id.date_month_view) as EditText
        yearView = view.findViewById<View>(R.id.date_year_view) as EditText
        cvvView = view.findViewById<View>(R.id.security_code_view) as EditText
        checkoutButton = view.findViewById<View>(R.id.button_checkout_order) as Button
        responseLayout = view.findViewById<View>(R.id.response_layout) as RelativeLayout
        responseTitle = view.findViewById<View>(R.id.encrypted_data_title) as TextView
        responseValue = view.findViewById<View>(R.id.encrypted_data_view) as TextView

        checkoutButton.setOnClickListener() {
            requestCaptureContext()
        }
    }

    private fun createToken(keyId: String) {
        //if (!areFormDetailsValid()) return;

        progressDialog = ProgressDialog.show(getActivity(), this.getString(R.string.progress_title),
                this.getString(R.string.progress_message), true);
        if (responseLayout.getVisibility() == View.VISIBLE) responseLayout.setVisibility(View.GONE);

        val payloadItems = getPayloadData()

        val cc = CaptureContext.fromJwt(keyId)
        var flexService = FlexService.getInstance()

        try {
            flexService.createTokenAsyncTask(cc, payloadItems, object :
                TransientTokenCreationCallback {
                override fun onSuccess(tokenResponse: TransientToken?) {

                    if (progressDialog.isShowing()) progressDialog.dismiss()

                    if (tokenResponse != null) {
                        responseValue.setText(tokenResponse.id)

                        moveToTokensResponseActivity(tokenResponse)
                    }
                }

                override fun onFailure(error: FlexException?) {
                    activity!!.runOnUiThread {
                        if (responseLayout.getVisibility() != View.VISIBLE) responseLayout.setVisibility(View.VISIBLE)
                        if (progressDialog.isShowing()) progressDialog.dismiss()

                        responseTitle.setText(getText(R.string.tokenError))
                        if (error != null) {
                            responseValue.setText(error.message)
                        }
                    }
                }
            })
        } catch (e: FlexException) {
            activity!!.runOnUiThread {
                responseTitle.setText(getText(R.string.tokenError))
                if (responseLayout.getVisibility() != View.VISIBLE) responseLayout.setVisibility(
                    View.VISIBLE
                )
                if (progressDialog.isShowing()) progressDialog.dismiss()

                responseValue.setText(e.toString())
            }
        }
    }

    private fun moveToTokensResponseActivity(response: TransientToken) {
        val intent = Intent(activity, TokensResponseActivity::class.java)
        intent.putExtra("TransientToken", response.id)
        startActivity(intent)
    }

    private fun getPayloadData(): Map<String, Any>? {
        val sad: MutableMap<String, Any> = HashMap()

        cardNumber = cardNumberView.text.toString().replace(" ", "")
        month = monthView.text.toString()
        cvv = cvvView.text.toString()
        year = yearView.text.toString()

        if (cardNumber!!.isNotEmpty()) {
            sad["paymentInformation.card.number"] = cardNumber!!
        }
        if (cvv!!.isNotEmpty()) {
            sad["paymentInformation.card.securityCode"] = cvv!!
        }
        if (month!!.isNotEmpty()) {
            sad["paymentInformation.card.expirationMonth"] = month!!
        }
        if (year!!.isNotEmpty()) {
            sad["paymentInformation.card.expirationYear"] = year!!
        }

        return sad
    }

    private fun setUpCreditCardEditText() {
        cardNumberView.addTextChangedListener(object : TextWatcher {
            private var spaceDeleted = false
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // check if a space was deleted
                val charDeleted = s.subSequence(start, start + count)
                spaceDeleted = " " == charDeleted.toString()
            }

            override fun afterTextChanged(editable: Editable) {
                // disable text watcher
                cardNumberView.removeTextChangedListener(this)

                // record cursor position as setting the text in the textview
                // places the cursor at the end
                val cursorPosition = cardNumberView.selectionStart
                val withSpaces = formatText(editable)
                cardNumberView.setText(withSpaces)
                // set the cursor at the last position + the spaces added since the
                // space are always added before the cursor
                cardNumberView.setSelection(cursorPosition + (withSpaces.length - editable.length))

                // if a space was deleted also deleted just move the cursor
                // before the space
                if (spaceDeleted) {
                    cardNumberView.setSelection(cardNumberView.selectionStart - 1)
                    spaceDeleted = false
                }

                // enable text watcher
                cardNumberView.addTextChangedListener(this)
            }

            private fun formatText(text: CharSequence): String {
                val formatted = StringBuilder()
                var count = 0
                for (i in 0 until text.length) {
                    if (Character.isDigit(text[i])) {
                        if (count % 4 == 0 && count > 0) formatted.append(" ")
                        formatted.append(text[i])
                        ++count
                    }
                }
                return formatted.toString()
            }
        })
    }

    private fun requestCaptureContext() {
        val service = FlexSessionServiceGenerator().getRetrofirApiService(Environment.SANDBOX)

        var cardData = FlexCardData(
                FlexFieldData(true), //card number
                FlexFieldData(true), //security code
                FlexFieldData(true), //expiration Month
                FlexFieldData(true), //expiration year
                FlexFieldData(false) //type
        )

        var paymentInfo = FlexPaymentInfo(cardData)
        val sessionFields = FlexSessionFields(paymentInfo)
        var requestObj = FlexSessionRequest(sessionFields)

        val gson = Gson()

        var merchantConfig = MerchantConfig()
        merchantConfig.merchantID = MERCHANT_ID
        merchantConfig.merchantKeyId = MERCHANT_KEY
        merchantConfig.merchantSecretKey = MERCHANT_SECRET

        merchantConfig.requestHost = Constants.HOSTCAS

        val jsonObjectString = gson.toJson(requestObj)
        merchantConfig.requestData = jsonObjectString

        val body = jsonObjectString.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        progressDialog = ProgressDialog.show(getActivity(), this.getString(R.string.progress_title),
                this.getString(R.string.session_progress_message), true);

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val keyGenerationResponse = service.createCaptureContext(
                        body,
                        getHeaderMapForCaptureContext(merchantConfig)
                )
                withContext(Dispatchers.Main) {
                    if (progressDialog.isShowing()) progressDialog.dismiss()

                    if (keyGenerationResponse.isSuccessful) {
                        keyGenerationResponse.body()?.let {
                            keyId = it
                            createToken(it)
                        }
                        print(keyGenerationResponse)
                    } else {
                        if (responseLayout.getVisibility() != View.VISIBLE) responseLayout.setVisibility(View.VISIBLE)
                        responseTitle.setText(getText(R.string.tokenError))
                        responseValue.setText("Capture context error...")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (progressDialog.isShowing()) progressDialog.dismiss()

                    if (responseLayout.getVisibility() != View.VISIBLE) responseLayout.setVisibility(View.VISIBLE)
                    responseTitle.setText(getText(R.string.tokenError))
                    responseValue.setText(e.localizedMessage)
                }
            }
        }
    }

    private fun getHeaderMapForCaptureContext(merchantConfig: MerchantConfig): Map<String, String> {
        val headerMap = mutableMapOf<String, String>()
        headerMap[Constants.V_C_MERCHANTID] = MERCHANT_ID
        headerMap[Constants.ACCEPT] = "application/jwt"
        headerMap["Content-Type"] = "application/json;charset=utf-8"
        headerMap[Constants.DATE] = PayloadUtility().getNewDate()
        headerMap[Constants.HOST] = Constants.HOSTCAS
        headerMap["Connection"] = "keep-alive"
        headerMap["User-Agent"] = "Android"

        val value = Authorization().getToken(merchantConfig)
        headerMap[Constants.SIGNATURE] = value

        val payloadDigest = PayloadDigest(merchantConfig)
        val digest = payloadDigest.getDigest()
        headerMap[Constants.DIGEST] = digest!!

        return headerMap
    }
}
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
import com.cybersource.flex.android.*
import com.cybersource.flex.android.CaptureContext.fromJwt

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

        progressDialog = ProgressDialog.show(getActivity(), this.getString(R.string.progress_title),
                this.getString(R.string.progress_message), true);
        if (responseLayout.getVisibility() == View.VISIBLE) responseLayout.setVisibility(View.GONE);

        val payloadItems = getPayloadData()

        /*
            This is the place where we convert the Capture context response to actual capture context object.
            Use FlexService->createTokenAsyncTask() method to generate the TransientToken. Input for createTokenAsyncTask()
            method will be capture context(cc) string and the event listeners. Note createTokenAsyncTask() is an asynchronous method.
         */
        val cc = fromJwt(keyId)
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

    /*
        Payload is must for creating transient token. The sample payload creation in this SampleApp is
        only for demonstration purpose, this should be changed according to your use cases.
    */
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
                cardNumberView.removeTextChangedListener(this)

                val cursorPosition = cardNumberView.selectionStart
                val withSpaces = formatText(editable)
                cardNumberView.setText(withSpaces)
                cardNumberView.setSelection(cursorPosition + (withSpaces.length - editable.length))

                if (spaceDeleted) {
                    cardNumberView.setSelection(cardNumberView.selectionStart - 1)
                    spaceDeleted = false
                }

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

    private fun moveToTokensResponseActivity(response: TransientToken) {
        val intent = Intent(activity, TokensResponseActivity::class.java)
        intent.putExtra("TransientToken", response.encoded)
        startActivity(intent)
    }

    private fun requestCaptureContext() {
        progressDialog = ProgressDialog.show(getActivity(), this.getString(R.string.progress_title),
            this.getString(R.string.session_progress_message), true);

        /*  WARNING:
            Before creating TransientToken make sure you have a valid capture context.
            And below creation of capture context code is for demonstration purpose only.
        */
        CaptureContextHelper().createCaptureContext(object :CaptureContextEvent {
            override fun onCaptureContextError(e: Exception) {
                if (progressDialog.isShowing()) progressDialog.dismiss()

                if (responseLayout.getVisibility() != View.VISIBLE) responseLayout.setVisibility(View.VISIBLE)
                responseTitle.setText(getText(R.string.tokenError))
                responseValue.setText(e.localizedMessage)
            }

            override fun onCaptureContextResponse(cc: String) {
                if (progressDialog.isShowing()) progressDialog . dismiss()

                keyId = cc
                createToken(cc)
                print(cc)
            }
        })
    }
}
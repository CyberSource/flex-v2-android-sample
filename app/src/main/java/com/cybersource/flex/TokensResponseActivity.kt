package com.cybersource.flex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView

class TokensResponseActivity : AppCompatActivity() {
    private lateinit var responseLayout: TextView
    private lateinit var repeatDemoButton: Button

    private var transientToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tokens_response)

        var bundle :Bundle? = intent.extras
        transientToken = bundle!!.getString("TransientToken")

        initialize()
    }

    private fun initialize() {
        responseLayout = findViewById(R.id.encrypted_data_view)
        responseLayout.setText(transientToken)

        repeatDemoButton = findViewById(R.id.repeatDemoBtn)

        repeatDemoButton.setOnClickListener() {
            finish()
        }
    }
}
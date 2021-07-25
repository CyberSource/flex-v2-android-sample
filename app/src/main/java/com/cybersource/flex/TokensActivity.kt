package com.cybersource.flex

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager


class TokensActivity : AppCompatActivity() {

    private val TAG_FRAGMENT_CHECKOUT = "TAG_FRAGMENT_CHECKOUT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tokens)

        setupUI()
        launchAcceptFragment()
    }

    private fun setupUI() {
        val textView = findViewById<View>(R.id.documentationLink) as TextView
        textView.isClickable = true
        textView.movementMethod = LinkMovementMethod.getInstance()
        val hyperLinkText = "<a href='https://github.com/CyberSource/flex-v2-android-sample'>See documentation link</a>"
        textView.text = Html.fromHtml(hyperLinkText, 1)
    }

    private fun launchAcceptFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        var checkoutFragment: FlexFragment? = fragmentManager.findFragmentByTag(
            TAG_FRAGMENT_CHECKOUT
        ) as? FlexFragment
        if (checkoutFragment == null) {
            val bundle = Bundle()
            checkoutFragment = FlexFragment()
            checkoutFragment.arguments = bundle
            fragmentManager.beginTransaction()
                    .replace(R.id.layout_container, checkoutFragment, TAG_FRAGMENT_CHECKOUT)
                    .commit()
        }
    }
}



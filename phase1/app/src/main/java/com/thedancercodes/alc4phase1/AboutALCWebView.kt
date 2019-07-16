package com.thedancercodes.alc4phase1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_alc.*

class AboutALCWebView: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_alc)
        setSupportActionBar(toolbar)

        // Load the site in the WebView
        about_alc_web_view.loadUrl("https://andela.com/alc/")
    }
}
package com.thedancercodes.alc4phase1

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.http.SslError
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.*
import kotlinx.android.synthetic.main.activity_about_alc.*

class AboutALCWebView: AppCompatActivity() {

    private val URL = "https://andela.com/alc/"
    private var isAlreadyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_alc)
        setSupportActionBar(toolbar)

        startLoaderAnimation()

        // To ensure we have JS running on our web page
        about_alc_web_view.settings.javaScriptEnabled = true

        // Set Up the WebView Client to modify some of the functionality
        about_alc_web_view.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                // for SSLErrorHandler
                val builder = AlertDialog.Builder(this@AboutALCWebView)
                builder.setMessage(R.string.notification_ssl_error)
                builder.setPositiveButton(
                    "continue"
                ) { dialog, which -> handler.proceed() }
                builder.setNegativeButton("cancel") { dialog, which ->
                    handler.cancel()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                endLoaderAnimation()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                endLoaderAnimation()
                showErrorDialog("Error",
                    "No Internet Connection. Please check your connection.",
                    this@AboutALCWebView)
            }
        }

        // Load the site in the WebView
        about_alc_web_view.loadUrl(URL)
    }

    override fun onResume() {
        super.onResume()

        if (isAlreadyCreated && !isNetworkAvailable()) {
            isAlreadyCreated = false
            showErrorDialog("Error",
                "No Internet Connection. Please check you connection",
                this@AboutALCWebView)
        }


    }

    private fun isNetworkAvailable(): Boolean {
        val connectionManager =
            this@AboutALCWebView.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectionManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    // Add Backward Navigation in the WebView
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && about_alc_web_view.canGoBack()) {
            about_alc_web_view.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showErrorDialog(title: String, message: String, context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("Cancel", { _, _ ->
            this@AboutALCWebView.finish()
        })
        dialog.setNeutralButton("Settings",  { _, _ ->
            // Add settings Activity
             startActivity(Intent(Settings.ACTION_SETTINGS))
        })
        dialog.setPositiveButton("Retry", { _, _ ->
            this@AboutALCWebView.recreate()
        })
        dialog.create().show()
    }

    private fun endLoaderAnimation() {
        loaderImage.clearAnimation()
        loaderImage.visibility = View.GONE
    }

    private fun startLoaderAnimation() {
        val objectAnimator = object : Animation() {
            /**
             * Helper for getTransformation. Subclasses should implement this to apply
             * their transforms given an interpolation value.  Implementations of this
             * method should always replace the specified Transformation or document
             * they are doing otherwise.
             *
             * @param interpolatedTime The value of the normalized time (0.0 to 1.0)
             * after it has been run through the interpolation function.
             * @param t The Transformation object to fill in with the current
             * transforms.
             */
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val startHeight = 170
                val newHeight = (startHeight * (startHeight + 40) * interpolatedTime).toInt()
                loaderImage.layoutParams.height = newHeight
                loaderImage.requestLayout() // Ensure it gets constantly changed
            }

            /**
             * Initialize this animation with the dimensions of the object being
             * animated as well as the objects parents. (This is to support animation
             * sizes being specified relative to these dimensions.)
             *
             *
             * Objects that interpret Animations should call this method when
             * the sizes of the object being animated and its parent are known, and
             * before calling [.getTransformation].
             *
             *
             * @param width Width of the object being animated
             * @param height Height of the object being animated
             * @param parentWidth Width of the animated object's parent
             * @param parentHeight Height of the animated object's parent
             */
            override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
                super.initialize(width, height, parentWidth, parentHeight)
            }

            /**
             *
             * Indicates whether or not this animation will affect the bounds of the
             * animated view. For instance, a fade animation will not affect the bounds
             * whereas a 200% scale animation will.
             *
             * @return true if this animation will change the view's bounds
             */
            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        objectAnimator.repeatCount = -1
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        objectAnimator.duration = 1000
        loaderImage.startAnimation(objectAnimator)
    }
}
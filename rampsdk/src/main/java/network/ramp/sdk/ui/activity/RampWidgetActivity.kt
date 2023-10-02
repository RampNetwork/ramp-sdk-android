package network.ramp.sdk.ui.activity


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ramp.sdk.databinding.WidgetActivityBinding
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampSDK.Companion.CONFIG_EXTRA
import timber.log.Timber
import network.ramp.sdk.events.model.*
import network.ramp.sdk.ui.webview.RampWidgetWebViewChromeClient.Companion.CAMERA_PERMISSION_REQUEST
import network.ramp.sdk.ui.webview.RampWidgetWebViewClient


internal class RampWidgetActivity : AppCompatActivity(), Contract.View {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uris = result.data?.data?.let {
            arrayOf(it)
        } ?: arrayOf()
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    lateinit var rampPresenter: RampPresenter

    private lateinit var binding: WidgetActivityBinding

    private lateinit var config: Config

    private val jsInterface = RampSdkJsInterface(
        onPostMessage = {
            rampPresenter.handlePostMessage(it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WidgetActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rampPresenter = RampPresenter(this)
        binding.webView.setupWebView(
            activity = this,
            wvClient = RampWidgetWebViewClient(binding.progressBar),
            jsInterface = jsInterface,
            fileChooserLauncher = fileChooserLauncher
        ) { filePathCallback = it }
        intent.extras?.getParcelable<Config>(CONFIG_EXTRA)?.let {
            config = it
        } ?: returnOnError("Config object cannot be null")

        if (savedInstanceState == null) {
            Timber.d(rampPresenter.buildUrl(config))
            securityCheck(intent)?.let {
                binding.webView.loadUrl(it)
            } ?: close()
        }
    }

    private fun securityCheck(intent: Intent): String? =
        if (isInternalIntent(intent) && rampPresenter.isUrlSafe(config.url))
            rampPresenter.buildUrl(config)
        else {
            Timber.e("SECURITY ALERT - UNAUTHORIZED CALL")
            null
        }

    private fun isInternalIntent(intent: Intent): Boolean =
        intent.data?.scheme == null


    override fun sendPostMessage(data: String) {
        val url = "javascript:(function f() { window.postMessage($data, \"*\"); })()"
        binding.webView.post {
            binding.webView.loadUrl(url)
        }
    }

    override fun showDialog() {
        //TODO() Show dialog in future
        close()
    }

    override fun close() {
        this.finish()
    }


    override fun onBackPressed() {
        rampPresenter.onBackPressed {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.removeJavascriptInterface(RampSdkJsInterface.RampSdkInterfaceName)
        scope.launch {
            EventBus.invokeEvent(WidgetClose())
        }
    }

    private fun returnOnError(message: String) {
        Timber.e(message)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    Timber.d("PERMISSION GRANTED ${permissions[i]}")
            }

        }
    }

    companion object {
        const val ACTION_VIEW_INTENT = "android.intent.action.VIEW"
    }
}

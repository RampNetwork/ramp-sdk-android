package network.ramp.sdk.ui.activity


import android.os.Bundle
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
import network.ramp.sdk.facade.RampSDK.Companion.URL_EXTRA
import network.ramp.sdk.ui.webview.RampWidgetWebViewClient


internal class RampWidgetActivity : AppCompatActivity(), Contract.View {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var rampPresenter: RampPresenter

    private lateinit var binding: WidgetActivityBinding

    private lateinit var config: Config

    private var url: String? = null

    private val jsInterface = RampSdkJsInterface(
        onPostMessage = {
            rampPresenter.handlePostMessage(it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WidgetActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        rampPresenter = RampPresenter(this, this)
        binding.webView.setupWebView(RampWidgetWebViewClient(binding.progressBar), jsInterface)

        intent.extras?.getParcelable<Config>(CONFIG_EXTRA)?.let {
            config = it
        } ?: returnOnError("Config object cannot be null")

        intent.extras?.getString(URL_EXTRA)?.let {
            url = it
        }

        if (savedInstanceState == null) {
            Timber.d(rampPresenter.buildUrl(config))
            binding.webView.loadUrl(url ?: rampPresenter.buildUrl(config))
        }
    }

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

    companion object {
        const val ACTION_VIEW_INTENT = "android.intent.action.VIEW"
        const val RAMP_PREFIX = "ramp"
        const val HTTPS_SCHEME = "https"
    }
}

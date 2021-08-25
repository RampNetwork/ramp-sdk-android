package network.ramp.sdk.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.ramp.sdk.databinding.WidgetActivityBinding
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampSDK.Companion.CONFIG_EXTRA
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import network.ramp.sdk.events.model.*
import network.ramp.sdk.ui.webview.RampWidgetWebViewClient


internal class RampWidgetActivity : AppCompatActivity(), Contract.View {

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

        rampPresenter = RampPresenter(this, this)
        binding.webView.setupWebView(RampWidgetWebViewClient(binding.progressBar), jsInterface)

        intent.extras?.getParcelable<Config>(CONFIG_EXTRA)?.let {
            config = it
        } ?: returnOnError("Config object cannot be null")

        if (savedInstanceState == null) {
            Timber.d(rampPresenter.buildUrl(config))
            binding.webView.loadUrl(rampPresenter.buildUrl(config))
        }

    }

    override fun sendPostMessage(data: String) {
        val url = "javascript:(function f() { window.postMessage($data, \"*\"); })()"
        binding.webView.post {
            binding.webView.loadUrl(url)
        }
    }

    override fun showDialog() {
        //Show dialog
    }

    override fun close() {
        this.finish()
    }


    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
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
        EventBus.getDefault().post(EventType.WIDGET_CLOSE)
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

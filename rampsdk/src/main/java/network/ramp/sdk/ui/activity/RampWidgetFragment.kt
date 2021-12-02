package network.ramp.sdk.ui.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ramp.sdk.databinding.WidgetFragmentBinding
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampSDK.Companion.CONFIG_EXTRA
import timber.log.Timber
import network.ramp.sdk.events.model.*
import network.ramp.sdk.ui.webview.RampWidgetWebViewClient


internal class RampWidgetFragment : Fragment(), Contract.View {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var rampPresenter: RampPresenter

    private lateinit var binding: WidgetFragmentBinding

    private lateinit var config: Config

    private val jsInterface = RampSdkJsInterface(
        onPostMessage = {
            rampPresenter.handlePostMessage(it)
        }
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WidgetFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        rampPresenter = RampPresenter(this, this.requireContext())
        binding.webView.setupWebView(RampWidgetWebViewClient(binding.progressBar), jsInterface)

        requireArguments().get(CONFIG_EXTRA)?.let {
            config = it as Config
        } ?: returnOnError("Config object cannot be null")

        if (savedInstanceState == null) {
            Timber.d(rampPresenter.buildUrl(config))
            binding.webView.loadUrl(rampPresenter.buildUrl(config))
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here

                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

        return view
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
//        this.finish()
    }


//    override fun onBackPressed() {
//        rampPresenter.onBackPressed {
//            super.onBackPressed()
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        binding.webView.restoreState(savedInstanceState)
//    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.removeJavascriptInterface(RampSdkJsInterface.RampSdkInterfaceName)
        scope.launch {
            EventBus.invokeEvent(WidgetClose())
        }
    }

    private fun returnOnError(message: String) {
        Timber.e(message)
//        finish()
    }

    companion object {
        const val ACTION_VIEW_INTENT = "android.intent.action.VIEW"
        const val RAMP_PREFIX = "ramp"
        const val HTTPS_SCHEME = "https"
    }
}

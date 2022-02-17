package network.ramp.sdk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ramp.sdk.databinding.WidgetActivityBinding
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.events.model.WidgetClose
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampSDK
import network.ramp.sdk.ui.activity.Contract
import network.ramp.sdk.ui.activity.RampPresenter
import network.ramp.sdk.ui.webview.RampWidgetWebViewClient
import timber.log.Timber

class RampWidgetFragment : Fragment(), Contract.View {


    companion object {
        fun newInstance(config: Config): RampWidgetFragment {
            val args = Bundle()
            args.putParcelable(RampSDK.CONFIG_EXTRA, config)
            val fragment = RampWidgetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var config: Config

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    internal lateinit var rampPresenter: RampPresenter

    private var _binding: WidgetActivityBinding? = null


    private val jsInterface = RampSdkJsInterface(
        onPostMessage = {
            rampPresenter.handlePostMessage(it)
        }
    )

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rampPresenter = RampPresenter(this, this.requireContext())

        binding.webView.setupWebView(RampWidgetWebViewClient(binding.progressBar), jsInterface)

        arguments?.getParcelable<Config>(RampSDK.CONFIG_EXTRA)?.let {
            config = it
        }

        if (savedInstanceState == null) {
            Timber.d(rampPresenter.buildUrl(config))
            binding.webView.loadUrl(rampPresenter.buildUrl(config))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = WidgetActivityBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webView.removeJavascriptInterface(RampSdkJsInterface.RampSdkInterfaceName)
        scope.launch {
            EventBus.invokeEvent(WidgetClose())
        }
        _binding = null
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            binding.webView.restoreState(it)
        }
    }

    override fun showDialog() {
        //TODO() Show dialog in future
        close()
    }

    override fun close() {
        (context as AppCompatActivity).supportFragmentManager.popBackStack()
    }

    override fun sendPostMessage(data: String) {
        val url = "javascript:(function f() { window.postMessage($data, \"*\"); })()"
        binding.webView.post {
            binding.webView.loadUrl(url)
        }
    }
}
package network.ramp.sdk.ui.activity

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import network.ramp.sdk.BuildConfig
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.model.*
import network.ramp.sdk.facade.Config
import network.ramp.sdk.utils.UrlSafeChecker
import timber.log.Timber

internal class RampPresenter(
    private val view: Contract.View
) : Contract.Presenter {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var configDone = false

    private val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Event::class.java, LABEL_KEY_TYPE)
                .withSubtype(OpenLink::class.java, EventType.OPEN_LINK.name)
                .withSubtype(WidgetClose::class.java, EventType.WIDGET_CLOSE.name)
                .withSubtype(PurchasedFailed::class.java, EventType.PURCHASE_FAILED.name)
                .withSubtype(PurchasedCreated::class.java, EventType.PURCHASE_CREATED.name)
                .withSubtype(WidgetConfigDone::class.java, EventType.WIDGET_CONFIG_DONE.name)
                .withSubtype(WidgetConfigFailed::class.java, EventType.WIDGET_CONFIG_FAILED.name)
                .withSubtype(BackButtonPressed::class.java, EventType.BACK_BUTTON_PRESSED.name)
                .withSubtype(KycInit::class.java, EventType.KYC_INIT.name)
                .withSubtype(KycStarted::class.java, EventType.KYC_STARTED.name)
                .withSubtype(KycFinished::class.java, EventType.KYC_FINISHED.name)
                .withSubtype(KycAborted::class.java, EventType.KYC_ABORTED.name)
                .withSubtype(KycSubmitted::class.java, EventType.KYC_SUBMITTED.name)
                .withSubtype(SendCrypto::class.java, EventType.SEND_CRYPTO.name)
                .withSubtype(SendCryptoResult::class.java, EventType.SEND_CRYPTO_RESULT.name)
                .withSubtype(
                    OfframpSaleCreated::class.java,
                    EventType.OFFRAMP_SALE_CREATED.name
                )
                .withSubtype(KycError::class.java, EventType.KYC_ERROR.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    init {
        handleEventsFromIntegrator()
    }

    private fun handleEventsFromIntegrator() {
        scope.launch {
            EventBus.events.collectLatest {
                when (it.type) {
                    EventType.SEND_CRYPTO_RESULT -> {
                        (it as? SendCryptoResult)?.payload?.let { payload ->
                            Timber.d("Integrator is sending SEND_CRYPTO_RESULT Event $payload")
                            postMessage(SendCryptoResult(payload))
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun handlePostMessage(json: String) {
        val event = moshi
            .adapter(Event::class.java)
            .fromJson(json)

        when (event?.type) {
            EventType.WIDGET_CLOSE -> {
                (event as? WidgetClose)?.payload?.let {
                    if (it.showAlert)
                        view.showDialog()
                    else {
                        view.close()
                    }
                }
            }
            EventType.OPEN_LINK -> {
                (event as? OpenLink)?.payload?.let {
                    Timber.d("onOpenUrl ${it.linkType} ${it.url} ")
                }

            }

            EventType.PURCHASE_FAILED -> {
                scope.launch {
                    EventBus.invokeEvent(PurchasedFailed(null))
                }
            }

            EventType.PURCHASE_CREATED -> {
                (event as? PurchasedCreated)?.payload?.let {
                    scope.launch {
                        EventBus.invokeEvent(PurchasedCreated(it))
                    }
                }
            }

            EventType.OFFRAMP_SALE_CREATED -> {
                (event as? OfframpSaleCreated)?.payload?.let {
                    scope.launch {
                        EventBus.invokeEvent(OfframpSaleCreated(it))
                    }
                }
            }

            EventType.SEND_CRYPTO -> {
                (event as? SendCrypto)?.payload?.let {
                    scope.launch {
                        EventBus.invokeEvent(SendCrypto(it))
                    }
                }
            }

            EventType.WIDGET_CONFIG_DONE -> {
                configDone = true
            }
            else -> Timber.w("Unhandled event $json")
        }
    }

    override fun buildUrl(config: Config): String {
        return config.url +
                "/?hostAppName=${config.hostAppName}" +
                "&hostLogoUrl=${config.hostLogoUrl}" +
                concatenateIfNotBlank("&swapAsset=", config.swapAsset) +
                concatenateIfNotBlank("&offrampAsset=", config.offrampAsset) +
                concatenateIfNotBlank("&swapAmount=", config.swapAmount) +
                concatenateIfNotBlank("&fiatCurrency=", config.fiatCurrency) +
                concatenateIfNotBlank("&fiatValue=", config.fiatValue) +
                concatenateIfNotBlank("&userAddress=", config.userAddress) +
                concatenateIfNotBlank("&userEmailAddress=", config.userEmailAddress) +
                concatenateIfNotBlank("&selectedCountryCode=", config.selectedCountryCode) +
                concatenateIfNotBlank("&defaultAsset=", config.defaultAsset) +
                concatenateIfNotBlank("&webhookStatusUrl=", config.webhookStatusUrl) +
                concatenateIfNotBlank("&hostApiKey=", config.hostApiKey) +
                concatenateIfNotBlank("&defaultFlow=", config.defaultFlow.name) +
                concatenateIfNotBlank("&offrampWebhookV3Url=", config.offrampWebhookV3Url) +
                concatenateIfNotBlank(
                    "&enabledFlows=",
                    config.enabledFlows.joinToString(separator = ",") { it.name }) +
                concatenateIfNotBlank(
                    "&useSendCryptoCallbackVersion=",
                    if (config.useSendCryptoCallback == true) Config.SEND_CRYPTO_CALLBACK_VERSION.toString() else ""
                ) +
                "&variant=$VARIANT" +
                "&deepLinkScheme=$DEEP_LINK_SCHEME" +
                "&sdkType=ANDROID" +
                "&sdkVersion=${BuildConfig.VERSION}"

    }


    fun onBackPressed(systemOnBackPressed: () -> Unit) {
        if (configDone)
            postMessage(
                BackButtonPressed("")
            )
        else
            systemOnBackPressed()
    }

    fun isUrlSafe(url: String): Boolean = UrlSafeChecker.isUrlSafe(url)

    fun <T : Event> postMessage(event: T) {
        val eventJson = moshi
            .adapter(Event::class.java)
            .toJson(event)
        view.sendPostMessage(eventJson)
    }

    private fun concatenateIfNotBlank(str1: String, str2: String): String {
        return if (str1.isNotBlank() && str2.isNotBlank()) {
            str1 + str2
        } else ""
    }

    companion object {
        const val VARIANT = "sdk-mobile"
        const val DEEP_LINK_SCHEME = "ramp"
        const val LABEL_KEY_TYPE = "type"
    }
}
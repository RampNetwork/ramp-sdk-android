package network.ramp.sdk.ui.activity

import android.content.Context
import com.passbase.passbase_sdk.PassbaseSDK
import com.passbase.passbase_sdk.PassbaseSDKListener
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.model.*
import network.ramp.sdk.facade.Config
import timber.log.Timber

internal class RampPresenter(
    private val view: Contract.View,
    private val context: Context
) : Contract.Presenter, PassbaseSDKListener {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var kycInitPayload: KycInitPayload? = null
    private var configDone = false

    private val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Event::class.java, LABEL_KEY_TYPE)
                .withSubtype(OpenLink::class.java, EventType.OPEN_LINK.name)
                .withSubtype(WidgetClose::class.java, EventType.WIDGET_CLOSE.name)
                .withSubtype(PurchasedFailed::class.java, EventType.PURCHASE_FAILED.name)
                .withSubtype(PurchasedCreated::class.java, EventType.PURCHASE_CREATED.name)
                .withSubtype(WidgetConfigDone::class.java, EventType.WIDGET_CONFIG_DONE.name)
                .withSubtype(BackButtonPressed::class.java, EventType.BACK_BUTTON_PRESSED.name)
                .withSubtype(KycInit::class.java, EventType.KYC_INIT.name)
                .withSubtype(KycStarted::class.java, EventType.KYC_STARTED.name)
                .withSubtype(KycFinished::class.java, EventType.KYC_FINISHED.name)
                .withSubtype(KycAborted::class.java, EventType.KYC_ABORTED.name)
                .withSubtype(KycSubmitted::class.java, EventType.KYC_SUBMITTED.name)
                .withSubtype(KycError::class.java, EventType.KYC_ERROR.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()


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
            EventType.KYC_INIT -> {
                (event as? KycInit)?.payload?.let {
                    Timber.d("kycInit $it ")
                    runPassbase(it)
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
            EventType.WIDGET_CONFIG_DONE -> {
                configDone = true
            }
            else -> Timber.w("Unhandled event $json")
        }
    }

    override fun buildUrl(config: Config): String {
        return config.url +
                "?hostAppName=${config.hostAppName}" +
                "&hostLogoUrl=${config.hostLogoUrl}" +
                concatenateIfNotBlank("&swapAsset=", config.swapAsset) +
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
                concatenateIfNotBlank("&enabledFlows=", config.enabledFlows.joinToString(separator = ",") { it.name }) +
                "&variant=$VARIANT" +
                "&deepLinkScheme=$DEEP_LINK_SCHEME"
    }

    private fun runPassbase(kycInit: KycInitPayload) {
        val passbaseRef = PassbaseSDK(context)

        kycInitPayload = kycInit
        passbaseRef.initialize(
            kycInit.apiKey
        )
        passbaseRef.prefillCountry = kycInit.countryCode
        passbaseRef.prefillUserEmail = kycInit.email
        kycInit.metaData?.let {
            passbaseRef.metaData = it
        }
        passbaseRef.callback(this)
        passbaseRef.startVerification()
    }

    override fun onError(errorCode: String) {

        Timber.e("Passbase onError $errorCode")

        val eventJson = moshi
            .adapter(Event::class.java)
            .toJson(
                when (errorCode) {
                    PASSBASE_CANCELLED_BY_USER -> KycAborted(
                        KycAbortedPayload(
                            kycInitPayload?.verificationId ?: 0
                        )
                    )
                    else -> KycError(KycErrorPayload(kycInitPayload?.verificationId ?: 0))
                }
            )

        view.sendPostMessage(eventJson)
    }

    override fun onFinish(identityAccessKey: String) {
        val eventJson = moshi
            .adapter(Event::class.java)
            .toJson(
                KycFinished(
                    KycFinishedPayload(
                        kycInitPayload?.verificationId ?: 0,
                        identityAccessKey
                    )
                )
            )
        view.sendPostMessage(eventJson)
    }

    override fun onSubmitted(identityAccessKey: String) {
        postMessage(
            KycSubmitted(
                KycSubmittedPayload(
                    kycInitPayload?.verificationId ?: 0,
                    identityAccessKey
                )
            )
        )
    }

    override fun onStart() {
        postMessage(
            KycStarted(
                KycStartedPayload(
                    kycInitPayload?.verificationId ?: 0
                )
            )
        )
    }

    fun onBackPressed(systemOnBackPressed: () -> Unit) {
        if (configDone)
            postMessage(
                BackButtonPressed("")
            )
        else
            systemOnBackPressed()
    }

    private fun <T : Event> postMessage(event: T) {
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
        const val PASSBASE_CANCELLED_BY_USER = "CANCELLED_BY_USER"
    }
}
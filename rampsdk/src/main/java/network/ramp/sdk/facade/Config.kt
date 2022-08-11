package network.ramp.sdk.facade

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Config(

    /**
     * A required string parameter that allows you to brand your Ramp integration with your app's name.
     * Example: "Maker DAO"
     */
    val hostAppName: String,


    /**
     * A required string parameter that allows you to brand your Ramp integration with your app's logo.
     * Example: "https://example.com/logo.png"
     */
    val hostLogoUrl: String,

    /**
     * An optional string parameter that allows you to use a non-production version of our widget.
     * Example: "https://ri-widget-staging.firebaseapp.com"
     */
    val url: String = "https://buy.ramp.network",

    /**
     * An optional string parameter that sets a list of available crypto assets for user to choose from.
     * If passed with a single asset, this parameter pre-sets the given asset for the user as the only option.
     * If passed as a list (for example, swapAsset="DAI,ETH,USDC"), it sets the available assets to the user with the first asset as the default selection.
     * If left blank, the user will choose an asset on their own from all available assets.
     * The list of all available assets can be accessed with GET /host-api/assets endpoint. [https://docs.ramp.network/rest-api-reference/#available-assets-and-prices]
     * Example: "ETH"
     */
    var swapAsset: String = "",

    /**
     * An optional int parameter that pre-sets the amount of crypto your user will buy.
     * If left blank, the user will choose the amount on their own.
     * Note: the amount should be provided in wei or token units.
     * Example: "10000000000000"
     */
    var swapAmount: String = "",


    /**
     * fiatCurrency (string) and fiatValue (int) are two optional parameters that allow you to pre-set the total fiat value of the purchase that will be suggested to the user.
     * They have to be used together as they don't work separately.
     * Note: This only sets the total fiat value of the transaction - actual currency used will depend on user's country and payment method.
     * Example: fiatCurrency: "GBP", fiatValue: "10"
     */
    var fiatCurrency: String = "",
    var fiatValue: String = "",

    /**
     * An optional string parameter that pre-sets the address the crypto will be sent to.
     * Example: "user blockchain address"
     */
    var userAddress: String = "",

    /**
     * An optional string parameter that pre-fills the email address for your user to make their onramping experience even quicker.
     * Example: "test@example.com"
     */
    var userEmailAddress: String = "",


    /**
     * An optional string parameter that pre-selects user country.
     * The value should be a two-letter country code (ISO 3166-1 alpha-2).
     * Note: If a user already has used Ramp, their country will be selected automatically based on their last used methods.
     * Example: "US"
     */
    var selectedCountryCode: String = "",


    /**
     * An optional string parameter that pre-selects an asset that will be shown to the user when they visit Ramp.
     * The user will be able to change the selected asset.
     * Example: "BTC"
     */
    var defaultAsset: String = "",


    /**
     * An optional string parameter that allows you to subscribe to events via webhooks [https://docs.ramp.network/webhooks].
     * Example: "https://example.com/webhook/"
     */
    var webhookStatusUrl: String = "",

    /**
     * An optional string parameter that allows our system to properly recognize and count purchases made through your API integration.
     * Example: "the API key you received"
     */
    var hostApiKey: String = "",

    var defaultFlow: Flow = Flow.ONRAMP,

    var enabledFlows: Set<Flow> = setOf(Flow.ONRAMP),

    var offrampWebhookV3Url: String = "",

    var useSendCryptoCallback: Boolean? = null,

    var useSendCryptoCallbackVersion: Int? = SEND_CRYPTO_CALLBACK_VERSION

) : Parcelable{

    companion object{
        const val SEND_CRYPTO_CALLBACK_VERSION = 1
    }
}
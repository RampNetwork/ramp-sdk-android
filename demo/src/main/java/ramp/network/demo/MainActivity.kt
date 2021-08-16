package ramp.network.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampCallback
import network.ramp.sdk.facade.RampSDK


class MainActivity : AppCompatActivity() {

    // 2. Initialize the SDK
    private lateinit var rampSdk: RampSDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rampSdk = RampSDK()

        button.setOnClickListener {
            // 3. Fill configuration object with your data
            val config = Config(
                hostLogoUrl = "https://ramp.network/assets/images/Logo.svg",
                hostAppName = "My App",
                userAddress = "0x4b7f8e04b82ad7f9e4b4cc9e1f81c5938e1b719f",
                url = "https://ri-widget-staging.firebaseapp.com/",
                swapAsset = "ETH",
                fiatCurrency = "USD",
                fiatValue = "10",
                selectedCountryCode = "US"
            )
            // 4. Implement callbacks
            val callback = object : RampCallback {
                override fun onPurchaseFailed() {

                }

                override fun onPurchaseCreated(
                    purchase: Purchase,
                    purchaseViewToken: String,
                    apiUrl: String
                ) {

                }

                override fun onWidgetClose() {

                }
            }
            // 5. Start widget
            rampSdk.startTransaction(this, config, callback)

        }
    }
}
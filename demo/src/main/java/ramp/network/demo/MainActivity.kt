package ramp.network.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import network.ramp.sdk.events.model.Asset
import network.ramp.sdk.events.model.OffRampSale
import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.Flow
import network.ramp.sdk.facade.RampCallback
import network.ramp.sdk.facade.RampSDK
import ramp.network.demo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 2. Initialize the SDK
    private lateinit var rampSdk: RampSDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        rampSdk = RampSDK()

        binding.button.setOnClickListener {
            // 3. Fill configuration object with your data
            val config = Config(
                hostLogoUrl = "https://ramp.network/assets/images/Logo.svg",
                hostAppName = "My App",
                userAddress = "0x4b7f8e04b82ad7f9e4b4cc9e1f81c5938e1b719f",
                url = "https://ri-widget-dev-5.firebaseapp.com/",
                defaultFlow = Flow.ONRAMP,
                enabledFlows = setOf(Flow.ONRAMP, Flow.OFFRAMP)
            )
            // 4. Implement callbacks
            val callback = object : RampCallback {
                override fun onPurchaseFailed() {
                    Log.d("MainActivity", "onPurchaseFailed")
                }

                override fun onPurchaseCreated(
                    purchase: Purchase,
                    purchaseViewToken: String,
                    apiUrl: String
                ) {
                    Log.d("MainActivity", "onPurchaseCreated")
                }

                override fun onWidgetClose() {
                    Log.d("MainActivity", "onWidgetClose")
                }

                override fun offrampSendCrypto(
                    assetInfo: Asset,
                    amount: String,
                    address: String
                ) {
                    Log.d(
                        "MainActivity",
                        "offrampSendCrypto  assetInfo: $assetInfo amount: $amount address: $address"
                    )
                }

                override fun onOffRampSaleCreated(
                    sale: OffRampSale,
                    saleViewToken: String,
                    apiUrl: String
                ) {
                    Log.d("MainActivity", "onOffRampSaleCreated ${sale.id} ${sale.createdAt} crypto: ${sale.crypto.amount} ${sale.crypto.assetInfo}")
                }
            }
            // 5. Start widget
            rampSdk.startTransaction(this, config, callback)
        }
    }
}
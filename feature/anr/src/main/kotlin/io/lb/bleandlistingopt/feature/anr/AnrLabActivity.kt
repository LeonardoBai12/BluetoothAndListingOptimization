package io.lb.bleandlistingopt.feature.anr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.lb.bleandlistingopt.core.designsystem.theme.BleLabTheme

class AnrLabActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleLabTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnrScreen()
                }
            }
        }
    }

    // Enabled/disabled around the foreground window, not just once in
    // onCreate -- see disableStrictMode() for why that distinction matters.
    override fun onResume() {
        super.onResume()
        enableStrictMode()
    }

    override fun onPause() {
        disableStrictMode()
        super.onPause()
    }
}

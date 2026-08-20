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
        enableStrictMode()
        setContent {
            BleLabTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnrScreen()
                }
            }
        }
    }
}

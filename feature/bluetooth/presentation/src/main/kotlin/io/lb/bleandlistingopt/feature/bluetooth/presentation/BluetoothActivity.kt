package io.lb.bleandlistingopt.feature.bluetooth.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.lb.bleandlistingopt.core.designsystem.theme.BleLabTheme
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies
import io.lb.bleandlistingopt.feature.bluetooth.presentation.di.DaggerBluetoothComponent

class BluetoothActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dependencies = application as BluetoothDependencies
        val component = DaggerBluetoothComponent.factory().create(dependencies)
        val viewModel = ViewModelProvider(this, component.viewModelFactory())[BluetoothViewModel::class.java]

        setContent {
            BleLabTheme {
                BluetoothScreen(viewModel)
            }
        }
    }
}

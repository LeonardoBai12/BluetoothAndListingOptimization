package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.lb.bleandlistingopt.core.designsystem.theme.BleLabTheme
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies
import io.lb.bleandlistingopt.feature.bluetooth.presentation.di.DaggerBluetoothComponent

private const val EXTRA_ADDRESS = "address"

class GattExplorerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dependencies = application as BluetoothDependencies
        val component = DaggerBluetoothComponent.factory().create(dependencies)
        val viewModel = ViewModelProvider(this, component.gattExplorerViewModelFactory())[GattExplorerViewModel::class.java]
        viewModel.load(intent.getStringExtra(EXTRA_ADDRESS).orEmpty())

        setContent {
            BleLabTheme {
                GattExplorerScreen(viewModel)
            }
        }
    }

    companion object {
        fun newIntent(context: Context, address: String): Intent =
            Intent(context, GattExplorerActivity::class.java).putExtra(EXTRA_ADDRESS, address)
    }
}

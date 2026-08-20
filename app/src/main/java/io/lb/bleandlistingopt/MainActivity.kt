package io.lb.bleandlistingopt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.lb.bleandlistingopt.core.designsystem.theme.BleLabTheme
import io.lb.bleandlistingopt.feature.anr.AnrLabActivity
import io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothActivity
import io.lb.bleandlistingopt.feature.listing.compose.optimized.OptimizedListActivity
import io.lb.bleandlistingopt.feature.listing.compose.unoptimized.UnoptimizedListActivity
import io.lb.bleandlistingopt.feature.listing.xml.optimized.OptimizedRecyclerActivity
import io.lb.bleandlistingopt.feature.listing.xml.unoptimized.UnoptimizedRecyclerActivity

private const val ROUTE_HOME = "home"
private const val ROUTE_LISTING_HUB = "listingHub"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleLabTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) { HomeScreen(navController) }
        composable(ROUTE_LISTING_HUB) { ListingHubScreen() }
    }
}

@Composable
private fun HomeScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Android Fundamentals Lab")
            Button(onClick = { navController.navigate(ROUTE_LISTING_HUB) }) {
                Text("Lists")
            }
            Button(onClick = { context.startActivity(Intent(context, BluetoothActivity::class.java)) }) {
                Text("Bluetooth")
            }
            Button(onClick = { context.startActivity(Intent(context, AnrLabActivity::class.java)) }) {
                Text("ANR Lab")
            }
        }
    }
}

@Composable
private fun ListingHubScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Listing Lab")
            Button(onClick = { context.startActivity(Intent(context, UnoptimizedListActivity::class.java)) }) {
                Text("Compose Unoptimized")
            }
            Button(onClick = { context.startActivity(Intent(context, OptimizedListActivity::class.java)) }) {
                Text("Compose Optimized")
            }
            Button(onClick = { context.startActivity(Intent(context, UnoptimizedRecyclerActivity::class.java)) }) {
                Text("XML Unoptimized")
            }
            Button(onClick = { context.startActivity(Intent(context, OptimizedRecyclerActivity::class.java)) }) {
                Text("XML Optimized")
            }
        }
    }
}

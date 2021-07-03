@file:Suppress("DEPRECATION")

package com.example.mobiledger.data.sources.internet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.net.ConnectivityManager.EXTRA_NETWORK_INFO
import android.net.NetworkInfo

class LegacyConnectivityProvider(
    context: Context,
    private val cm: ConnectivityManager,
    private val onNewState: (NetworkState) -> Unit
) : ConnectivityProvider {

    private val receiver = ConnectivityReceiver()

    init {
        context.registerReceiver(receiver, IntentFilter(CONNECTIVITY_ACTION))
        // context.unregisterReceiver(receiver)
    }

    override fun getNetworkState(): NetworkState {
        val activeNetworkInfo = cm.activeNetworkInfo
        return if (activeNetworkInfo != null) {
            NetworkState.ConnectedState.ConnectedLegacy(activeNetworkInfo)
        } else {
            NetworkState.NotConnectedState
        }
    }

    private inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {

            val networkInfo = cm.activeNetworkInfo
            val fallbackNetworkInfo: NetworkInfo? = intent.getParcelableExtra(EXTRA_NETWORK_INFO)
            // a set of dirty workarounds
            val state: NetworkState =
                if (networkInfo?.isConnectedOrConnecting == true) {
                    NetworkState.ConnectedState.ConnectedLegacy(networkInfo)
                } else if (networkInfo != null && fallbackNetworkInfo != null &&
                    networkInfo.isConnectedOrConnecting != fallbackNetworkInfo.isConnectedOrConnecting
                ) {
                    NetworkState.ConnectedState.ConnectedLegacy(fallbackNetworkInfo)
                } else {
                    val state = networkInfo ?: fallbackNetworkInfo
                    if (state != null) NetworkState.ConnectedState.ConnectedLegacy(state) else NetworkState.NotConnectedState
                }
            onNewState(state)
        }
    }
}
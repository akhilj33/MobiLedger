package com.example.mobiledger.data.sources.internet

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class NewConnectivityProvider(
    private val cm: ConnectivityManager,
    private val onNewState: (NetworkState) -> Unit
) :
    ConnectivityProvider {

    private val networkCallback: ConnectivityCallback = ConnectivityCallback()

    init {
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun getNetworkState(): NetworkState {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null) {
            NetworkState.ConnectedState.Connected(capabilities)
        } else {
            NetworkState.NotConnectedState
        }
    }

    private inner class ConnectivityCallback : NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            onNewState(NetworkState.ConnectedState.Connected(capabilities))
        }

        override fun onLost(network: Network) {
            onNewState(NetworkState.NotConnectedState)
        }
    }
}
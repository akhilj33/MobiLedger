package com.example.mobiledger.data.sources.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface InternetSource {
    fun emitInternetStatus(): SharedFlow<Boolean>
    fun getInternetStatus(): Boolean
}

class InternetSourceImpl(context: Context, scope: CoroutineScope = GlobalScope) : InternetSource {
    private val connectivityProvider: ConnectivityProvider
    private val internetEvents = MutableSharedFlow<Boolean>(2, 0, BufferOverflow.DROP_OLDEST)
    private val listener: ((NetworkState) -> Unit) = { scope.launch { handleNewState(it) } }

    init {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityProvider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NewConnectivityProvider(cm, listener)
        } else {
            LegacyConnectivityProvider(context, cm, listener)
        }
    }


    override fun emitInternetStatus(): SharedFlow<Boolean> {
        return internetEvents.asSharedFlow()
    }

    override fun getInternetStatus(): Boolean {
        return getInternetStatusBool(connectivityProvider.getNetworkState())
    }

    private suspend fun handleNewState(state: NetworkState) {
        val currentState = getInternetStatusBool(state)
        if (internetEvents.replayCache.isEmpty() || internetEvents.replayCache.last() != currentState) {
            internetEvents.emit(currentState)
        }
    }

    private fun getInternetStatusBool(state: NetworkState): Boolean {
        return when (state) {
            is NetworkState.NotConnectedState -> false
            is NetworkState.ConnectedState -> state.hasInternet
        }
    }
}

interface ConnectivityProvider {
    fun getNetworkState(): NetworkState
}

sealed class NetworkState {
    object NotConnectedState : NetworkState()

    sealed class ConnectedState(val hasInternet: Boolean) : NetworkState() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        data class Connected(val capabilities: NetworkCapabilities) :
            ConnectedState(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))

        @Suppress("DEPRECATION")
        data class ConnectedLegacy(val networkInfo: NetworkInfo) : ConnectedState(networkInfo.isConnectedOrConnecting)
    }
}

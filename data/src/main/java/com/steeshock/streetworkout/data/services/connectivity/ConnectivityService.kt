package com.steeshock.streetworkout.data.services.connectivity

import javax.inject.Inject


class ConnectivityService @Inject constructor() : IConnectivityService {

    companion object {
        private const val PING_COMMAND = "/system/bin/ping -c 1 www.google.com"
    }

    // Not good solution on some device manufactures
    override fun isConnectedToInternet(): Boolean {
        return try {
            val ipProcess: Process = Runtime.getRuntime().exec(PING_COMMAND)
            val exitValue = ipProcess.waitFor()
            ipProcess.destroy()
            exitValue == 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
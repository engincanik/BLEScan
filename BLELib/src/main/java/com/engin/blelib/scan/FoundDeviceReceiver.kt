package com.engin.blelib.scan

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDevice, Short) -> Unit
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                val rssi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getShortExtra(
                        BluetoothDevice.EXTRA_RSSI,
                        Short.MAX_VALUE
                    )
                } else {
                    intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MAX_VALUE)
                }
                if (device != null && rssi != Short.MAX_VALUE) onDeviceFound(device, rssi)
            }
        }
    }
}
package com.engin.blelib.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.engin.blelib.model.ScannedDevice

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): ScannedDevice {
    return ScannedDevice(
        name = name,
        address = address,
        rssi = Short.MAX_VALUE,
        timestamp = java.util.Date().time
    )
}
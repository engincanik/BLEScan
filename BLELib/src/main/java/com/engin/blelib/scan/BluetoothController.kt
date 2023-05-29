package com.engin.blelib.scan

import com.engin.blelib.model.ScannedDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<ScannedDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()
}
package com.engin.blescan.repo

import com.engin.blelib.model.ScannedDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    fun startScan(): StateFlow<List<ScannedDevice>>
    fun stopStan()
}
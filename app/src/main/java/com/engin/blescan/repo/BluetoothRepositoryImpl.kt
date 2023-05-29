package com.engin.blescan.repo

import com.engin.blelib.model.ScannedDevice
import com.engin.blelib.scan.BluetoothController
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class BluetoothRepositoryImpl @Inject constructor(
    private val bluetoothController: BluetoothController
) : BluetoothRepository {
    override fun startScan(): StateFlow<List<ScannedDevice>> {
        bluetoothController.startDiscovery()
        return bluetoothController.scannedDevices
    }

    override fun stopStan() {
        bluetoothController.stopDiscovery()
    }
}
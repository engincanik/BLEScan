package com.engin.blelib.scan

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.engin.blelib.model.ScannedDevice
import com.engin.blelib.util.toBluetoothDeviceDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<ScannedDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<ScannedDevice>>
        get() = _scannedDevices

    private val foundDeviceReceiver = FoundDeviceReceiver { device, rssi ->
        val newDevice = device.toBluetoothDeviceDomain()
        newDevice.rssi = rssi
        _scannedDevices.update { devices ->
            if (newDevice in devices && rssi == Short.MAX_VALUE) devices else devices + newDevice
        }

    }

    override fun startDiscovery() {
        _scannedDevices.value = emptyList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                !hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
            ) return
        } else {
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                !hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
            ) return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                !hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
            ) return
        } else {
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                !hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
            ) return
        }

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}
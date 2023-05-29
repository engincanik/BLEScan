package com.engin.blescan.usecases

import androidx.lifecycle.LiveData
import com.engin.blelib.model.ScannedDevice
import com.engin.blescan.repo.BluetoothRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetScannedDevicesUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepository
) {
    operator fun invoke(): StateFlow<List<ScannedDevice>> = bluetoothRepository.startScan()
}
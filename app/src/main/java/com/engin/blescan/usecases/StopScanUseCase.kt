package com.engin.blescan.usecases

import com.engin.blescan.repo.BluetoothRepository
import javax.inject.Inject

class StopScanUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepository
) {
    operator fun invoke() {
        bluetoothRepository.stopStan()
    }
}
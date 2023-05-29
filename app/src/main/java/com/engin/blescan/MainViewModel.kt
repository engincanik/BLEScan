package com.engin.blescan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.engin.blescan.repo.BluetoothRepository
import com.engin.blescan.usecases.GetScannedDevicesUseCase
import com.engin.blescan.usecases.StopScanUseCase
import com.engin.blescan.util.ScanRate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getScannedDevicesUseCase: GetScannedDevicesUseCase,
    private val stopScanUseCase: StopScanUseCase
): ViewModel(), MainVMContract.ViewModel {

    private val _uiStateData = MutableLiveData<MainVMContract.State.GetScannedDevices>()
    override val uiStateData: LiveData<MainVMContract.State.GetScannedDevices> = _uiStateData

    private val _scanRate = MutableLiveData(ScanRate.HIGH)
    val scanRate: LiveData<ScanRate> = _scanRate

    private val _isSortedAscending = MutableLiveData(true)
    val isSortedAscending: LiveData<Boolean> = _isSortedAscending

    override fun invokeAction(action: MainVMContract.Action) {
        when (action) {
            MainVMContract.Action.StartScan -> startScan()
            MainVMContract.Action.StopScan -> stopScan()
            is MainVMContract.Action.SetScanRate -> setScanRate(action.scanRate)
            MainVMContract.Action.SetSort -> setSort()
        }
    }

    private fun startScan() {
        _uiStateData.value = MainVMContract.State.GetScannedDevices(getScannedDevicesUseCase().asLiveData())
    }

    private fun stopScan() {
        stopScanUseCase()
    }

    private fun setScanRate(scanRate: ScanRate) {
        _scanRate.value = scanRate
    }

    private fun setSort() {
        _isSortedAscending.value = !(_isSortedAscending.value ?: false)
    }

}
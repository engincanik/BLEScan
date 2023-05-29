package com.engin.blescan

import androidx.lifecycle.LiveData
import com.engin.blelib.model.ScannedDevice
import com.engin.blescan.util.ScanRate

sealed class MainVMContract {

    interface ViewModel {
        val uiStateData: LiveData<State.GetScannedDevices>
        fun invokeAction(action: Action)
    }

    sealed class State {
        data class GetScannedDevices(val device: LiveData<List<ScannedDevice>>): State()
    }

    sealed class Action {
        object StartScan: Action()
        object StopScan: Action()
        data class SetScanRate(val scanRate: ScanRate): Action()
        object SetSort: Action()
    }
}
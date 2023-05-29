package com.engin.blescan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.engin.blescan.usecases.GetScannedDevicesUseCase
import com.engin.blescan.usecases.StopScanUseCase
import com.engin.blescan.util.ScanRate
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getScannedDevicesUseCase: GetScannedDevicesUseCase

    @MockK
    private lateinit var stopScanUseCase: StopScanUseCase

    private lateinit var mainViewModel: MainViewModel

    @MockK(relaxed = true)
    private lateinit var uiStateObserver: Observer<MainVMContract.State.GetScannedDevices>

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mainViewModel = MainViewModel(getScannedDevicesUseCase, stopScanUseCase)
        mainViewModel.uiStateData.observeForever(uiStateObserver)
    }

    @After
    fun tearDown() {
        mainViewModel.uiStateData.removeObserver(uiStateObserver)
    }

    @Test
    fun invokeAction_setScanRate_shouldUpdateScanRateLiveData() {
        val scanRate = ScanRate.HIGH

        mainViewModel.invokeAction(MainVMContract.Action.SetScanRate(scanRate))

        assert(scanRate == mainViewModel.scanRate.value)
    }

    @Test
    fun invokeAction_setSort_shouldToggleIsSortedAscendingLiveData() {
        val initialIsSortedAscending = mainViewModel.isSortedAscending.value

        mainViewModel.invokeAction(MainVMContract.Action.SetSort)

        val updatedIsSortedAscending = mainViewModel.isSortedAscending.value
        assert(!(initialIsSortedAscending ?: false) == updatedIsSortedAscending)
    }
}

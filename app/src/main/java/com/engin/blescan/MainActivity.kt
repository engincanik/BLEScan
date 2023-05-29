package com.engin.blescan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.engin.blelib.model.ScannedDevice
import com.engin.blescan.databinding.ActivityMainBinding
import com.engin.blescan.util.DeviceListAdapter
import com.engin.blescan.util.ScanRate
import com.engin.blescan.util.hasPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var deviceListAdapter: DeviceListAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var isScanning = false
    private var textChangeJob: Job? = null
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setLaunchers()
        preparePermissions()
        prepareSpinner()

        deviceListAdapter = DeviceListAdapter()
        binding.rvDeviceList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceListAdapter
        }

        handleLiveData()
        binding.btnToggleScan.setOnClickListener {
            if (checkBluetoothEnabled()) toggleScan()
        }
        binding.btnSort.setOnClickListener {
            viewModel.invokeAction(MainVMContract.Action.SetSort)
            binding.btnSort.text = getString(
                if (viewModel.isSortedAscending.value == true) R.string.asc
                else R.string.desc
            )
            deviceListAdapter.sortItemsByValue(viewModel.isSortedAscending.value ?: true)
        }
    }


    private fun toggleScan() {
        if (isScanning) {
            stopScan()
            binding.btnToggleScan.text = getString(R.string.start)
        } else {
            startScan()
            binding.btnToggleScan.text = getString(R.string.stop)
        }
    }

    private fun startScan() {
        isScanning = true
        deviceListAdapter.clear()
        viewModel.invokeAction(MainVMContract.Action.StartScan)
        textChangeJob?.cancel()
        textChangeJob = CoroutineScope(Dispatchers.Main).launch {
            delay(viewModel.scanRate.value?.level ?: ScanRate.LOW.level)
            withContext(Dispatchers.Main) {
                stopScan()
                binding.btnToggleScan.text = getString(R.string.start)
            }
        }
    }

    private fun stopScan() {
        isScanning = false
        viewModel.invokeAction(MainVMContract.Action.StopScan)
    }

    private fun prepareSpinner() {
        val intervalOptions = arrayOf(ScanRate.LOW.name, ScanRate.MEDIUM.name, ScanRate.HIGH.name)
        val intervalAdapter =
            ArrayAdapter(this, R.layout.custom_spinner_item, intervalOptions)
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInterval.apply {
            adapter = intervalAdapter
            onItemSelectedListener = IntervalItemSelectedListener()
        }
    }


    inner class IntervalItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent?.getItemAtPosition(position) as String) {
                ScanRate.LOW.name -> setScanRate(ScanRate.LOW)
                ScanRate.MEDIUM.name -> setScanRate(ScanRate.MEDIUM)
                ScanRate.HIGH.name -> setScanRate(ScanRate.HIGH)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }
    }

    private fun setScanRate(scanRate: ScanRate) {
        viewModel.invokeAction(MainVMContract.Action.SetScanRate(scanRate))
    }

    private fun handleLiveData() {
        viewModel.uiStateData.observe(this) {
            it.device.observe(this) { list ->
                sortItemsByValue(list.filter { device ->
                    device.name != null && device.rssi != Short.MAX_VALUE
                })

            }
        }
    }

    private fun setLaunchers() {
        enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else {
                true
            }

            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }
    }

    private fun preparePermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }
    }

    private fun checkBluetoothEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (bluetoothAdapter?.isEnabled != true || !Manifest.permission.BLUETOOTH_SCAN.hasPermission(this)) {
                preparePermissions();
                showPermissionDeniedMessage()
                return false;
            }
        } else {
            if (bluetoothAdapter?.isEnabled != true) {
                preparePermissions();
                showPermissionDeniedMessage()
                return false;
            }
        }
        return true;
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, "Permissions denied or Bluetooth closed", Toast.LENGTH_SHORT).show()
    }

    private fun sortItemsByValue(items: List<ScannedDevice>) {
        var tempItems: List<ScannedDevice>
        tempItems = items.sortedBy { it.timestamp }
        if (viewModel.isSortedAscending.value != true) {
            tempItems = items.reversed()
        }
        deviceListAdapter.setItems(tempItems.toMutableList())
    }

    override fun onDestroy() {
        super.onDestroy()
        textChangeJob?.cancel()
    }
}
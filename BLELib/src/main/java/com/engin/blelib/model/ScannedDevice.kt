package com.engin.blelib.model

data class ScannedDevice(
    val name: String?,
    val address: String,
    var rssi: Short = Short.MAX_VALUE,
    val timestamp: Long
)

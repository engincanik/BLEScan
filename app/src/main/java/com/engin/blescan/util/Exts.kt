package com.engin.blescan.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.PermissionChecker.checkSelfPermission
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDateString(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US)
    val date = Date(this)
    return sdf.format(date)
}

fun String.hasPermission(context: Context): Boolean {
    return context.checkSelfPermission(this) == PackageManager.PERMISSION_GRANTED
}
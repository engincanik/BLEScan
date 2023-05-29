package com.engin.blescan.util

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.engin.blelib.model.ScannedDevice
import com.engin.blescan.R
import com.engin.blescan.databinding.ItemBleDeviceBinding

class DeviceListAdapter :
    RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private var items: List<ScannedDevice> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            ItemBleDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = items[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int = items.size

    inner class DeviceViewHolder(private val bleView: ItemBleDeviceBinding) :
        RecyclerView.ViewHolder(bleView.root) {
        fun bind(device: ScannedDevice) {
            with(bleView) {
                tvDeviceName.text = device.name
                tvRssi.text = root.context.getString(R.string.rssi_info, device.rssi.toString())
                tvTimestamp.text =
                    root.context.getString(R.string.timestamp_info, device.timestamp.toFormattedDateString())
                ivRssi.setImageResource(
                    when (device.rssi.toInt()) {
                        in Short.MAX_VALUE downTo -50 -> R.drawable.ic_wifi
                        in -51..60 -> R.drawable.ic_wifi_good
                        in -61..70 -> R.drawable.ic_wifi_medium
                        in -71 downTo Short.MIN_VALUE -> R.drawable.ic_wifi_weak
                        else -> R.drawable.ic_wifi_weak
                    }
                )
            }
        }
    }

    fun setItems(newList: MutableList<ScannedDevice>) {
        val diffResult = DiffUtil.calculateDiff(
            DeviceListItemDiffCallback(
                items.distinctBy { it.name },
                newList.distinctBy { it.name }
            )
        )
        items = newList.distinctBy { it.name }
        diffResult.dispatchUpdatesTo(this)
    }

    fun sortItemsByValue(isSortedAscending: Boolean) {
        setItems(
            if (!isSortedAscending) items.sortedBy { it.timestamp }.reversed().toMutableList()
            else items.sortedBy { it.timestamp }.toMutableList()
        )
    }

    fun clear() {
        val mutableList = items.toMutableList()
        mutableList.clear()
        setItems(mutableList)
    }
}

class DeviceListItemDiffCallback(
    private val oldList: List<ScannedDevice>,
    private val newList: List<ScannedDevice>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].address == newList[newItemPosition].address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
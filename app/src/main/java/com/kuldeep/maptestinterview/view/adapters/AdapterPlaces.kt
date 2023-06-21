package com.kuldeep.maptestinterview.view.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kuldeep.maptestinterview.databinding.RowItemPlaceBinding
import com.kuldeep.maptestinterview.room.PlaceEntity
import com.kuldeep.maptestinterview.utils.OnItemClick

class AdapterPlaces(
    private val list: ArrayList<PlaceEntity>,
    private val onEdit: OnItemClick<Long>,
    private val onDelete: OnItemClick<Long>
): RecyclerView.Adapter<AdapterPlaces.Holder>() {

    class Holder(itemView: View, val binding: RowItemPlaceBinding) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(
            binding.root, binding
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val binding = holder.binding

        binding.cityName.text = list[position].cityName
        binding.address.text = list[position].address
        binding.distance.text = String.format("%.2f", list[position].distance) +" KM"

        binding.edit.setOnClickListener {
            Log.d("DISTANCE",list[position].distance.toString())
            onEdit.onClick(list[position].id ?: -1)

        }

        binding.delete.setOnClickListener {
            onDelete.onClick(list[position].id ?: -1)
        }
    }

    override fun getItemCount() = list.size

}
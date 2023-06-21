package com.kuldeep.maptestinterview.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kuldeep.maptestinterview.room.PlaceEntity
import com.kuldeep.maptestinterview.utils.OnItemClick

class AdapterSearchPlace(private val list: List<PlaceEntity>, private val onItemClick: OnItemClick<PlaceEntity>): RecyclerView.Adapter<AdapterSearchPlace.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val city = holder.itemView.findViewById<TextView>(android.R.id.text1)
        val address = holder.itemView.findViewById<TextView>(android.R.id.text2)

        city.text = list[position].cityName
        address.text = list[position].address

        holder.itemView.setOnClickListener {
            onItemClick.onClick(list[position])
        }

    }

    override fun getItemCount() = list.size

}
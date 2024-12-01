package com.example.graph_app.presentation.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.graph_app.R
import com.example.graph_app.databinding.ItemCoordinateBinding
import com.example.graph_app.domain.model.PointModel

class CoordinatesAdapter : RecyclerView.Adapter<CoordinatesAdapter.ViewHolder>() {

    var items: List<PointModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            val list = value.toMutableList()
            list.add(0, PointModel("x", "y"))
            field = list
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_coordinate, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCoordinateBinding.bind(itemView)

        fun bind(point: PointModel) {
            binding.x.text = point.x
            binding.y.text = point.y
        }
    }
}
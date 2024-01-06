package com.example.coffeeapp.presentation.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.ItemCafesBinding
import com.example.coffeeapp.domain.entities.CafeEntity

class CafesAdapter(
    private val onItemClick: (itemId: Int) -> Unit
) : ListAdapter<CafeEntity, CafesAdapter.CafesViewHolder>(CafesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafesViewHolder {
        val binding = ItemCafesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CafesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CafesViewHolder, position: Int) {
        val item = getItem(position)
        with (holder.binding) {
            name.text = item.name
            if (item.distanceMeters != null) {
                distance.text = decorateDistance(item.distanceMeters, distance.context)
                distance.visibility = View.VISIBLE
            } else distance.visibility = View.GONE
        }
    }

    /** Item blink fix */
    override fun onBindViewHolder(
        holder: CafesViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            val bundle = (payloads[0] as Bundle)
            val distance = bundle.getFloat(PAYLOAD_DISTANCE)
            with (holder.binding.distance) {
                visibility = View.VISIBLE
                text = decorateDistance(distance, this.context)
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    inner class CafesViewHolder(
        val binding: ItemCafesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = getItem(adapterPosition)
                onItemClick(item.id)
            }
        }
    }

    private class CafesDiffUtil : DiffUtil.ItemCallback<CafeEntity>() {
        override fun areItemsTheSame(oldItem: CafeEntity, newItem: CafeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CafeEntity, newItem: CafeEntity): Boolean {
            return oldItem == newItem
        }

        /** Item blink fix */
        override fun getChangePayload(oldItem: CafeEntity, newItem: CafeEntity): Any {
            return Bundle().apply {
                newItem.distanceMeters?.let { putFloat(PAYLOAD_DISTANCE, it) }
            }
        }
    }

    private fun decorateDistance(distance: Float, context: Context): String {
        return if (distance >= 1000.0f) {
            context.getString(
                R.string.distance_kilometers_format,
                (distance / 1000).toInt().toString()
            )
        } else {
            context.getString(
                R.string.distance_meters_format,
                distance.toInt().toString()
            )
        }
    }

    companion object {
        private const val PAYLOAD_DISTANCE = "distance_payload"
    }
}
package com.example.coffeeapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HeaderViewListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.databinding.ItemCafesBinding
import com.example.coffeeapp.domain.entities.CafeEntity

class CafesAdapter(
    private val data: List<CafeEntity>,
    private val onItemClick: (itemId: Int) -> Unit
) : RecyclerView.Adapter<CafesAdapter.CafesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafesViewHolder {
        val binding = ItemCafesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CafesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CafesViewHolder, position: Int) {
        val item = data[position]
        holder.binding.name.text = item.name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CafesViewHolder(
        val binding: ItemCafesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = data[adapterPosition]
                onItemClick(item.id)
            }
        }
    }
}
package com.example.coffeeapp.presentation.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.ItemCartBinding
import com.example.coffeeapp.databinding.ItemProductsBinding
import com.example.coffeeapp.domain.entities.ProductEntity
import com.squareup.picasso.Picasso

class CartProductsAdapter(
    private val onItemCountChanged: (itemId: Int, count: Int) -> Unit,
) : ListAdapter<ProductEntity, CartProductsAdapter.CartProductsViewHolder>(CardProductsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            name.text = item.name
            val context = holder.binding.root.context
            price.text = context.getString(R.string.item_price_wrapper, item.price.toString())
            count.text = item.count.toString()
        }
    }

    //item blink fix
    override fun onBindViewHolder(
        holder: CartProductsViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            val bundle = (payloads[0] as Bundle)
            holder.binding.count.text = bundle.getInt(PAYLOAD_QUANTITY).toString()
        } else { super.onBindViewHolder(holder, position, payloads) }
    }

    inner class CartProductsViewHolder(
        val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.increment.setOnClickListener {
                val item = getItem(adapterPosition)
                onItemCountChanged(item.id, item.count + 1)
            }
            binding.decrement.setOnClickListener {
                val item = getItem(adapterPosition)
                if (item.count > 0) onItemCountChanged(item.id, item.count - 1)
            }
        }
    }

    private class CardProductsDiffUtil : DiffUtil.ItemCallback<ProductEntity>() {

        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem == newItem
        }

        //item blink fix
        override fun getChangePayload(oldItem: ProductEntity, newItem: ProductEntity): Any? {
            return Bundle().apply {
                putInt(PAYLOAD_QUANTITY, newItem.count)
            }
        }
    }

    companion object {
        private const val PAYLOAD_QUANTITY = "quantity_payload"
    }
}
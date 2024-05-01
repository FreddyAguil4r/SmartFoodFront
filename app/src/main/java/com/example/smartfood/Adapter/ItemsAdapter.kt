package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.ProductResponseI
import com.example.smartfood.R
import com.google.android.material.card.MaterialCardView


class ItemsAdapter(private val categoryList: List<ProductResponseI>)
    :RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.childTitleTv)
        var textCantidad: TextView
        val cardView: MaterialCardView = itemView.findViewById(R.id.childCardView)
        init {
            textCantidad = itemView.findViewById(R.id.textAmount)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_layout,parent,false)
        return ItemsViewHolder(view)
    }
    override fun getItemCount(): Int {
        return categoryList.size
    }
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val cat = categoryList[position]
        holder.title.text = categoryList[position].name
        holder.textCantidad.text = cat.amount.toInt().toString()

        if (cat.amount.toInt() < 15) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.md_theme_light_onErrorContainer))
            holder.textCantidad.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.md_theme_light_tertiaryContainer))
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.md_theme_light_tertiaryContainer))
            holder.textCantidad.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.md_theme_light_onPrimaryContainer))
        }
    }
}
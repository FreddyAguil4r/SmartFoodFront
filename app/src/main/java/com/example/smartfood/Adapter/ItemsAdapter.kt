package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Model.Category
import com.example.smartfood.Model.Product
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.ModelResponse.ProductResponseI
import com.example.smartfood.R

class ItemsAdapter(private val categoryList: List<ProductResponseI>):RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.childTitleTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_layout,parent,false)
        return ItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.title.text = categoryList[position].name
    }
}
package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.CategoryResponseI

import com.example.smartfood.R

class ItemContainerAdapter(private val categoryList: List<CategoryResponseI>
                           , private val imageList: List<Int>) :
    RecyclerView.Adapter<ItemContainerAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logoIv: ImageView = itemView.findViewById(R.id.parentLogoIv)
        val titleTv: TextView = itemView.findViewById(R.id.parentTitleTv)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.langRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories_main_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val parentItem = categoryList[position]
        if (imageList.size > position) {
            holder.logoIv.setImageResource(imageList[position])
        }else {
            holder.logoIv.setImageResource(R.drawable.df)
        }
        holder.titleTv.text = parentItem.categoryName

        if (parentItem.products?.isNotEmpty() == true) {
            holder.childRecyclerView.visibility = View.VISIBLE
            holder.childRecyclerView.setHasFixedSize(true)
            holder.childRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 3)
            val adapter = ItemsAdapter(parentItem.products)
            holder.childRecyclerView.adapter = adapter
        } else {
            holder.childRecyclerView.visibility = View.GONE
        }
    }
}
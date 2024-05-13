package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.CategoryTotal
import com.example.smartfood.R


class TrendingAdapter(private val categoryList: List<CategoryTotal>):
    RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var textTitle : TextView
        var textValue : TextView
        init {
            textTitle = itemView.findViewById(R.id.item_category)
            textValue = itemView.findViewById(R.id.tvValue_category)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_monitor_categories,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: TrendingAdapter.ViewHolder, position: Int) {
        val sup = categoryList[position]
        holder.textTitle.text = sup.name
        holder.textValue.text = sup.totalCategory.toString()
    }

}
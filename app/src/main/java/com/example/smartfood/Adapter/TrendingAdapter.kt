package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.R


class TrendingAdapter(private val suplierList: List<CategoryResponse>): RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    //representa identidad indivual
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
        return suplierList.size
    }

    override fun onBindViewHolder(holder: TrendingAdapter.ViewHolder, position: Int) {
        val sup = suplierList[position]
        holder.textTitle.text = sup.name
        //Redondear a dos digitos.  holder.textValue.text = sup.totalValuesCategories.toString()
        val roundedValue = String.format("%.2f", sup.totalValuesCategories)
        holder.textValue.text = roundedValue.toString()
    }
}
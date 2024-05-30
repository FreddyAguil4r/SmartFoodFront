package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.ProductDemand
import com.example.smartfood.R

class SortedAdapter(private val productListSorted : List<ProductDemand>)
    :RecyclerView.Adapter<SortedAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textProductName : TextView
        var textForecastDemand : TextView
        init {
            textProductName = itemView.findViewById(R.id.text_product_name)
            textForecastDemand = itemView.findViewById(R.id.text_forecast_demand)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_sorted, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = productListSorted.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productListSorted[position]
        holder.textProductName.text = item.productName
        holder.textForecastDemand.text = "Cantidad a comprar: ${item.cantidadComprar.toString()}"
    }
}
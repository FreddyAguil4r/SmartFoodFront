package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.ProductResponseBigQuery
import com.example.smartfood.R

class SeasonalDemandAdapter(private val categoryListForecast : List<ProductResponseBigQuery>):
RecyclerView.Adapter<SeasonalDemandAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textProductName : TextView
        var textForecastDemand : TextView
        init {
            textProductName = itemView.findViewById(R.id.text_product_name_bimensual)
            textForecastDemand = itemView.findViewById(R.id.text_bimensualforecast_demand)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_bimonhtly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = categoryListForecast.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categoryListForecast[position]
        holder.textProductName.text = item.productName.toString()
        holder.textForecastDemand.text = item.cantidadComprar.toString()
    }
}
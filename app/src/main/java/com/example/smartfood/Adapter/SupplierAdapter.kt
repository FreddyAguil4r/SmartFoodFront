package com.example.smartfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.R

class SupplierAdapter(private val suplierList: List<SupplierResponse>, private val deleteSuppliers: (Int) -> Unit): RecyclerView.Adapter<SupplierAdapter.ViewHolder>() {

    //representa identidad indivual
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var textTitle : TextView
        var textRuc: TextView
        var textAddress : TextView
        var editButton : Button
        var deleteButton : Button
        init {
            textTitle = itemView.findViewById(R.id.text_supplier_name)
            textRuc = itemView.findViewById(R.id.text_supplier_ruc)
            textAddress = itemView.findViewById(R.id.text_supplier_address)
            editButton = itemView.findViewById(R.id.edit_button)
            deleteButton = itemView.findViewById(R.id.delete_button)
        }
    }

    //su función es inflar la interfaz de usuario de un elemento de la lista crea una instancia de ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_supplier,parent,false)
        return ViewHolder(v)
    }

    //Se encarga de vincular los datos de un conjunto de datos a la interfaz de usuario de un elemento específico en la lista
    override fun onBindViewHolder(holder: SupplierAdapter.ViewHolder, position: Int) {
        val sup = suplierList[position]
        holder.textTitle.text = sup.name
        holder.textRuc.text = "RUC: ${sup.ruc}"
        holder.textAddress.text = "Dirección: ${sup.address}"
        holder.deleteButton.setOnClickListener {
            deleteSuppliers(sup.id)
        }
    }

    //Informar al RecyclerView sobre la cantidad de elementos que se mostrarán.
    override fun getItemCount(): Int {
        return suplierList.size
    }
}
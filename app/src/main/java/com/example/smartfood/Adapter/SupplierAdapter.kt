package com.example.smartfood.Adapter

import android.app.AlertDialog
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.R
import com.example.smartfood.Request.SupplierRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupplierAdapter(private val suplierList: List<SupplierResponse>,
                      private val deleteSuppliers: (Int) -> Unit,
                      private val updateSuppliers: suspend (Int, SupplierRequest) -> Unit)
    : RecyclerView.Adapter<SupplierAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_supplier,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SupplierAdapter.ViewHolder, position: Int) {
        val sup = suplierList[position]
        holder.textTitle.text = sup.name
        holder.textRuc.text = "RUC: ${sup.ruc}"
        holder.textAddress.text = "Dirección: ${sup.address}"

        holder.deleteButton.setOnClickListener {
            deleteSuppliers(sup.id)
        }

        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_update_supplier,null)

            // Configura el diálogo con un constructor de AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            val saveButton = dialogView.findViewById<Button>(R.id.btnSaveUpdate)
            val cancelButton = dialogView.findViewById<Button>(R.id.btnCancelUpdate)

            val newNameEditText = dialogView.findViewById<EditText>(R.id.editNewTextNombre)
            val newRucEditText = dialogView.findViewById<EditText>(R.id.editNewTextRUC)
            val newAddressEditText = dialogView.findViewById<EditText>(R.id.editNewTextDireccion)

            newNameEditText.text = Editable.Factory.getInstance().newEditable(sup.name)
            newRucEditText.text = Editable.Factory.getInstance().newEditable(sup.ruc)
            newAddressEditText.text = Editable.Factory.getInstance().newEditable(sup.address)

            saveButton.setOnClickListener {
                val newName = newNameEditText.text.toString()
                val newRuc = newRucEditText.text.toString()
                val newAddress = newAddressEditText.text.toString()
                val updatedSupplier = SupplierRequest(newName, newRuc, newAddress)

                CoroutineScope(Dispatchers.Main).launch {
                    updateSuppliers(sup.id, updatedSupplier)
                }
                alertDialog.dismiss()
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return suplierList.size
    }
}
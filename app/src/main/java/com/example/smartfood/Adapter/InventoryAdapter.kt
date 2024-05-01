package com.example.smartfood.Adapter

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.InventoryFragment
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.R
import com.example.smartfood.Request.UpdateProductRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryAdapter(private val productList: List<ProductResponse>,
                       private val deleteProducts: (Int) -> Unit,
                       private val updateProducts : suspend (Int, UpdateProductRequest) -> Unit)
    :RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var editButton: ImageButton
        var deleteButton: ImageButton
        var textCantidad: TextView
        var textFecha: TextView

        init {
            textTitle = itemView.findViewById(R.id.item_product)
            textCantidad = itemView.findViewById(R.id.quantityTextView)
            textFecha = itemView.findViewById(R.id.dateTextView)
            editButton = itemView.findViewById(R.id.edit_button)
            deleteButton = itemView.findViewById(R.id.delete_button)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_layout_inventory, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sup = productList[position]
        holder.textTitle.text = sup.name
        holder.textCantidad.text = "Cantidad: ${sup.amount.toInt()}"
        holder.textFecha.text = "Fecha de compra: ${sup.datePurchase.toString()}"

        holder.deleteButton.setOnClickListener {
            deleteProducts(sup.id)
        }
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_layout_update_product, null)

            // Configura el diálogo con un constructor de AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            val saveButton = dialogView.findViewById<Button>(R.id.btnSaveUpdate)
            val cancelButton = dialogView.findViewById<Button>(R.id.btnCancelUpdate)

            val newNameEditText = dialogView.findViewById<EditText>(R.id.editNewTextNombre)
            val newUnitCostEditText = dialogView.findViewById<EditText>(R.id.editNewTextUnitCost)
            val newAmountEditText = dialogView.findViewById<EditText>(R.id.editNewTextAmount)

            newNameEditText.text = Editable.Factory.getInstance().newEditable(sup.name)
            newUnitCostEditText.text =
                Editable.Factory.getInstance().newEditable(sup.unitCost.toString())

            val amountWithoutDecimal = String.format("%.0f", sup.amount)
            newAmountEditText.text = Editable.Factory.getInstance().newEditable(amountWithoutDecimal)

            saveButton.setOnClickListener {
                val newName = newNameEditText.text.toString()
                val newUnitCost = newUnitCostEditText.text.toString().toDouble()
                val newAmount = newAmountEditText.text.toString().toDouble()
                val updatedProduct = UpdateProductRequest(
                    newName,
                    newUnitCost,
                    newAmount,
                )
                CoroutineScope(Dispatchers.Main).launch {
                    updateProducts(sup.id, updatedProduct)
                    alertDialog.dismiss()
                }
            }
            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        if(sup.amount < 15){
            showLowQuantityNotification(holder.itemView.context, sup.name)
        }

    }

    private fun showLowQuantityNotification(context: Context, productName: String) {
        val notificationId = 1 // ID único para la notificación

        val builder = NotificationCompat.Builder(context, InventoryFragment.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Atención!")
            .setContentText("La cantidad de $productName es baja. ¡Considere reabastecerse!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
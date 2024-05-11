package com.example.smartfood.Adapter

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.InventoryFragment
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.ModelResponse.UnitResponse
import com.example.smartfood.R
import com.example.smartfood.Request.ProductRequest
import com.example.smartfood.Request.PurchaseRequest
import com.example.smartfood.Request.UpdateProductRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryAdapter(private val productList: List<ProductResponse>,
                       private val deleteProducts: (Int) -> Unit,
                       private val updateProducts : suspend (Int, UpdateProductRequest) -> Unit,
                       private val makePurchase : (PurchaseRequest) -> Unit,
                       private val dataSpinnerSupplier : List<String>,
                       private val supplierList: List<SupplierResponse>,
                       private val dataSpinnerUnits : List<String>,
                       private val unitList: List<UnitResponse>
):RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private var idSupplier=0
    private var idUnit=0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var editButton: ImageButton
        var deleteButton: ImageButton
        var purchaseProductButton : Button
        var removeProductButton : Button
        var textCantidad: TextView
        var textFecha: TextView
        init {
            textTitle = itemView.findViewById(R.id.item_product)
            textCantidad = itemView.findViewById(R.id.quantityTextView)
            textFecha = itemView.findViewById(R.id.dateTextView)
            editButton = itemView.findViewById(R.id.edit_button)
            deleteButton = itemView.findViewById(R.id.delete_button)
            purchaseProductButton = itemView.findViewById(R.id.addProductButton)
            removeProductButton = itemView.findViewById(R.id.removeProductButton)
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
        holder.textTitle.text = sup.productName
        holder.textCantidad.text = "Cantidad: ${sup.quantity.toString()}"


        holder.deleteButton.setOnClickListener {
            deleteProducts(sup.productId)
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
            newNameEditText.text = Editable.Factory.getInstance().newEditable(sup.productName)


            saveButton.setOnClickListener {
                val newName = newNameEditText.text.toString()
                val updatedProduct = UpdateProductRequest(newName)
                CoroutineScope(Dispatchers.Main).launch {
                    updateProducts(sup.productId, updatedProduct)
                    alertDialog.dismiss()
                }
            }
            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        holder.purchaseProductButton.setOnClickListener {
            val context = holder.itemView.context
            val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_layout_purchase_product,null)

            val spinnnerSuplier : TextInputLayout = dialogView.findViewById(R.id.supplierSpinner)
            val spinnnerUnit : TextInputLayout = dialogView.findViewById(R.id.unitSpinner)
            val adapterSpSuplier = ArrayAdapter(context,android.R.layout.simple_spinner_item,dataSpinnerSupplier)
            val adapterSpUnits = ArrayAdapter(context,android.R.layout.simple_spinner_item,dataSpinnerUnits)
            (spinnnerSuplier.editText as AutoCompleteTextView).setAdapter(adapterSpSuplier)
            (spinnnerUnit.editText as AutoCompleteTextView).setAdapter(adapterSpUnits)

            (spinnnerSuplier.editText as AutoCompleteTextView).onItemClickListener=
                AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val selec=adapterView.getItemAtPosition(i)
                val idCapturado=supplierList.filter { it.name==selec }[0].id
                idSupplier=idCapturado
                Log.i("ITEMSEL","$idCapturado")
            }

            (spinnnerUnit.editText as AutoCompleteTextView).onItemClickListener=
                AdapterView.OnItemClickListener { adapterView, view, i, l ->
                    val selec=adapterView.getItemAtPosition(i)
                    val idCapturado=unitList.filter { it.name==selec }[0].id
                    idUnit=idCapturado
                    Log.i("ITEMSEL","$idCapturado")
                }

            val alertDialog = MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.title))
                .setView(dialogView)
                .create()

            val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
            val addPurchaseButton: Button = dialogView.findViewById(R.id.addButton)

            addPurchaseButton.setOnClickListener {
                val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
                val unitCostEditText = dialogView.findViewById<EditText>(R.id.unitCostEditText)
                val amount = amountEditText.text.toString().toInt()
                val unitCost = unitCostEditText.text.toString().toDouble()

                val purchaseRequest = PurchaseRequest(amount,unitCost,sup.productId,idSupplier,idUnit)
                makePurchase(purchaseRequest)
                alertDialog.dismiss()
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
        holder.removeProductButton.setOnClickListener {

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
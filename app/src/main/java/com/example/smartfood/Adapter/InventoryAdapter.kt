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
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.InventoryFragment
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.ModelResponse.UnitResponse
import com.example.smartfood.R
import com.example.smartfood.Request.PurchaseRequest
import com.example.smartfood.Request.SubstractProductRequest
import com.example.smartfood.Request.UpdateProductRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class InventoryAdapter(private var productList: List<ProductResponse>,
                       private val deleteProducts: (Int) -> Unit,
                       private val updateProducts : suspend (Int, UpdateProductRequest) -> Unit,
                       private val makePurchase : (PurchaseRequest) -> Unit,
                       private val substractPurchase: (SubstractProductRequest) -> Unit,
                       private val dataSpinnerSupplier : List<String>,
                       private val supplierList: List<SupplierResponse>,
                       private val dataSpinnerUnits : List<String>,
                       private val unitList: List<UnitResponse>
):RecyclerView.Adapter<InventoryAdapter.ViewHolder>(),Filterable {

    private val lowStockProducts = mutableListOf<String>()
    private val highStockProducts = mutableListOf<String>()
    private var idSupplier=0
    private var idUnit=0
    var filteredProductList = mutableListOf<ProductResponse>().apply {
        addAll(productList)
    }

    fun updateList(newProducts: List<ProductResponse>) {
        this.productList = newProducts
        this.filteredProductList.clear()
        this.filteredProductList.addAll(newProducts)
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var editButton: Button
        var deleteButton: Button
        var purchaseProductButton : Button
        var removeProductButton : Button
        var textCantidad: TextView
        var editTextSubstract: EditText = itemView.findViewById(R.id.editTextSubstract)

        init {
            checkStockProducts()
            textTitle = itemView.findViewById(R.id.item_product)
            textCantidad = itemView.findViewById(R.id.quantityTextView)
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
        return filteredProductList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sup = filteredProductList[position]
        holder.textTitle.text = sup.productName
        holder.textCantidad.text = "Cantidad en stock: ${sup.quantity.toString()}"

        if (sup.quantity < 30) {
            //lowStockProducts.add(sup.productName)
            showLowQuantityNotification(holder.itemView.context)
        }

        if (sup.quantity > 2000) {
            showHighQuantityNotification(holder.itemView.context)
        }

        holder.deleteButton.setOnClickListener {
            deleteProducts(sup.productId)
        }
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_layout_update_product, null)

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
                val amount = amountEditText.text.toString().toIntOrNull()
                val unitCost = unitCostEditText.text.toString().toDoubleOrNull()

                // Validar que los valores no sean nulos y no sean negativos
                if (amount == null || unitCost == null) {
                    Toast.makeText(context, "Por favor, ingrese valores válidos.", Toast.LENGTH_SHORT).show()
                } else if (amount < 0 || unitCost < 0) {
                    Toast.makeText(context, "Los valores no pueden ser negativos.", Toast.LENGTH_SHORT).show()
                } else {
                    // Si las validaciones pasan, proceder con la solicitud de compra
                    val purchaseRequest = PurchaseRequest(amount, unitCost, sup.productId, idSupplier, idUnit)
                    makePurchase(purchaseRequest)
                    alertDialog.dismiss()
                }
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
        holder.removeProductButton.setOnClickListener {
            val cantidadARestarStr = holder.editTextSubstract.text.toString()
            if (cantidadARestarStr.isNotEmpty()) {
                val cantidadARestar = cantidadARestarStr.toInt()
                val subtractProductRequest = SubstractProductRequest(sup.productId, cantidadARestar)
                substractPurchase(subtractProductRequest)
            } else {
                Toast.makeText(holder.itemView.context, "Por favor, ingresa la cantidad a restar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkStockProducts() {
        for (product in productList) {
            if (product.quantity < 30) {
                lowStockProducts.add(product.productName)
            }
            if (product.quantity > 2000) {
                highStockProducts.add(product.productName)
            }
        }
    }

    private fun showLowQuantityNotification(context: Context) {
        val notificationId = 1

        val builder = NotificationCompat.Builder(context, InventoryFragment.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Atención!")
            .setContentText("Los siguientes productos tienen bajo stock: ${lowStockProducts.joinToString(", ")}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Los siguientes productos tienen bajo stock: ${lowStockProducts.joinToString(", ")}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.md_theme_light_surface))

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private fun showHighQuantityNotification(context: Context) {
        val notificationId = 2

        val builder = NotificationCompat.Builder(context, InventoryFragment.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Atención!")
            .setContentText("Los siguientes productos tienen alto stock: ${highStockProducts.joinToString(", ")}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Los siguientes productos tienen alto stock: ${highStockProducts.joinToString(", ")}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                val filterResults = FilterResults()

                // Si no hay búsqueda, devuelve la lista completa.
                if (charSearch.isEmpty()) {
                    filterResults.values = productList
                } else {
                    // Filtra por RUC.
                    filterResults.values = productList.filter {
                        it.productName.toLowerCase(Locale.getDefault()).contains(charSearch.toLowerCase(
                            Locale.getDefault()))
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.values?.let {
                    @Suppress("UNCHECKED_CAST")
                    filteredProductList = it as MutableList<ProductResponse>
                    notifyDataSetChanged()
                }
            }
        }
    }
}
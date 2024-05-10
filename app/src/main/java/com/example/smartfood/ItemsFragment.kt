package com.example.smartfood

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartfood.Adapter.ItemContainerAdapter
import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.ModelResponse.CategoryResponseI
import com.example.smartfood.ModelResponse.UnitResponse
import com.example.smartfood.Request.CategoryRequest
import com.example.smartfood.Request.ProductRequest
import com.example.smartfood.Service.APIServiceCategory
import com.example.smartfood.Service.APIServiceProduct
import com.example.smartfood.Service.APIServiceUnit
import com.example.smartfood.databinding.FragmentItemsBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemsFragment : Fragment() {
    private lateinit var binding: FragmentItemsBinding
    private lateinit var adapter: ItemContainerAdapter
    private val unitList = mutableListOf<UnitResponse>()
    private val categoryList = mutableListOf<CategoryResponse>()
    private val categoryListI = mutableListOf<CategoryResponseI>()
    private val dataSpinnerCategory = mutableListOf<String>()
    private val dataSpinnerUnits = mutableListOf<String>()
    private var idCategory=0
    private var idUnit=0

    val imageList = listOf(
        R.drawable.carne,
        R.drawable.verduras,
        R.drawable.cereal,
        R.drawable.suministros,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemsBinding.inflate(inflater)

        binding.rcyView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemContainerAdapter(categoryListI, imageList)
        binding.rcyView.adapter = adapter

        searchAllCategoriesWithProduct()

        binding.fabAddCategory.setOnClickListener { openDialogAddNewCategory() }
        binding.floatingButton.setOnClickListener { openDialogAddNewProduct() }
        return binding.root
    }

    private fun openDialogAddNewCategory() {
        val dialogView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_add_category, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        dialogView.findViewById<Button>(R.id.btn_add_category).setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.edt_new_category).text.toString()
            val categoryRequest = CategoryRequest(nombre)
            addCategory(categoryRequest)
            alertDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_cancel_category).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun openDialogAddNewProduct() {
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_add_product, null)
        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
        val saveNewProductButton: Button = dialogView.findViewById(R.id.btnSave)

        searchAllCategories()
        searchAllUnits()
        val spinnerCategory : TextInputLayout = dialogView.findViewById(R.id.spinner_category)
        val spinnerSupplier : TextInputLayout = dialogView.findViewById(R.id.spinner_unit)
        val adapterSpCategory = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,dataSpinnerCategory)
        val adapterSpSupplier = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,dataSpinnerUnits)
        (spinnerCategory.editText as AutoCompleteTextView).setAdapter(adapterSpCategory)
        (spinnerSupplier.editText as AutoCompleteTextView).setAdapter(adapterSpSupplier)

        (spinnerCategory.editText as AutoCompleteTextView).onItemClickListener=AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val selec=adapterView.getItemAtPosition(i)
            val idCapturado=categoryList.filter { it.name==selec }[0].id
            idCategory=idCapturado
            Log.i("ITEMSEL","$idCapturado")
        }

        (spinnerSupplier.editText as AutoCompleteTextView).onItemClickListener=AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val selec=adapterView.getItemAtPosition(i)
            val idCapturado=unitList.filter { it.abbrevation==selec }[0].id
            idUnit=idCapturado

            Log.i("ITEMSEL","$selec")
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        saveNewProductButton.setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.edtName).text.toString()
           //val costo_unitarioStr = dialogView.findViewById<EditText>(R.id.edtUnitCost).text.toString()
           //val cantidadStr = dialogView.findViewById<EditText>(R.id.edtAmount).text.toString()
           //val costo_unitario = costo_unitarioStr.toDoubleOrNull() ?: 0.0
           //val cantidad = cantidadStr.toDoubleOrNull() ?: 0.0
            val productRequest = ProductRequest(nombre, idCategory, idUnit)
            addProduct(productRequest)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://26.54.240.231:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun addCategory(categoryRequest: CategoryRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                getRetrofit().create(APIServiceCategory::class.java).addCategory(categoryRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    searchAllCategories()
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun addProduct(productRequest: ProductRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                getRetrofit().create(APIServiceProduct::class.java).addProduct(productRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    searchAllCategoriesWithProduct()
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun searchAllCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceCategory::class.java)
                .getAllCategories("category/all")
            val sup = call.body()
            //Variable donde esta la respuesta
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val categories = sup ?: emptyList()
                    categoryList.clear()
                    categoryList.addAll(categories)
                    adapter.notifyDataSetChanged()

                    dataSpinnerCategory.clear()
                    dataSpinnerCategory.addAll(categories.map { it.name })
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun searchAllUnits(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceUnit::class.java)
                .getAllUnits("unit/all")
            val sup = call.body()
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val units = sup ?: emptyList()
                    unitList.clear()
                    unitList.addAll(units)
                    adapter.notifyDataSetChanged()

                    dataSpinnerUnits.clear()
                    dataSpinnerUnits.addAll(unitList.map { it.name })
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun searchAllCategoriesWithProduct(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = getRetrofit().create(APIServiceCategory::class.java)
                    .getAllCategoriesWithProducts("category/products/quantity")
                val sup = call.body()
                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val categories = sup ?: emptyList()
                        categoryListI.clear()
                        categoryListI.addAll(categories)
                        adapter.notifyDataSetChanged()
                    } else {
                        showError(10)
                    }
                }
            } catch (e: Exception) {
                if (retryCount < 3) { // Número de reintentos
                    delay(2000) // Tiempo de espera antes de reintento, en milisegundos
                    searchAllCategoriesWithProduct(retryCount + 1)
                } else {
                    withContext(Dispatchers.Main) {
                        showError(10)
                    }
                }
            }
        }
    }
    private fun showError(retryCount: Int) {
        if (retryCount >= 3) {
            Toast.makeText(requireContext(), "Error de conexión. Por favor, verifica tu conexión a internet.", Toast.LENGTH_LONG).show()
        } else {
            // Muestra un diálogo de progreso aquí
            val progressDialog = ProgressDialog(requireContext()).apply {
                setTitle("Cargando")
                setMessage("Intentando reconectar...")
                setCancelable(false) // para que no se pueda cancelar
            }
            progressDialog.show()

            // Oculta el diálogo después de un tiempo determinado o cuando se complete la carga
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000) // Espera antes de ocultar el diálogo
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    searchAllCategoriesWithProduct(retryCount + 1)
                }
            }
        }
    }

}
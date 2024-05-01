package com.example.smartfood

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.example.smartfood.Adapter.CategoryAdapter
import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.ModelResponse.CategoryResponseI
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.Request.CategoryRequest
import com.example.smartfood.Request.ProductRequest
import com.example.smartfood.Service.APIServiceCategory
import com.example.smartfood.Service.APIServiceProduct
import com.example.smartfood.Service.APIServiceSupplier
import com.example.smartfood.databinding.FragmentItemsBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ItemsFragment : Fragment() {
    private lateinit var binding: FragmentItemsBinding
    private lateinit var adapter: CategoryAdapter
    private val categoryList = mutableListOf<CategoryResponse>()
    private val categoryListI = mutableListOf<CategoryResponseI>()
    private val supplierList = mutableListOf<SupplierResponse>()
    private val dataSpinnerCategory = mutableListOf<String>()
    private val dataSpinnerSupplier = mutableListOf<String>()
    private var idCategory=0
    private var idSupplier=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemsBinding.inflate(inflater)
        binding.rcyView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoryAdapter(categoryListI)
        binding.rcyView.adapter = adapter
        searchAllCategoriesWithProducts()

        binding.fabAddCategory.setOnClickListener { openDialogAddNewCategory() }
        binding.floatingButton.setOnClickListener { openDialogAddNewProduct() }
        return binding.root
    }

    private fun openDialogAddNewCategory() {
        val dialogView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_category, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        dialogView.findViewById<Button>(R.id.btn_add_category).setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.edt_new_category).text.toString()
            val categoryRequest = CategoryRequest(nombre)
            //AGREGANDO UNA CATEGORIA CON EL ENDPOINT
            addCategory(categoryRequest)
            alertDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_cancel_category).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun openDialogAddNewProduct() {
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_items, null)
        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
        val saveButton: Button = dialogView.findViewById(R.id.btnSave)

        //SPINNERS
        searchAllCategories()
        searchAllSupplier()
        val spinnerCategory : TextInputLayout = dialogView.findViewById(R.id.spinner_category)
        val spinnerSupplier : TextInputLayout = dialogView.findViewById(R.id.spinner_supplier)
        val adapterSpCategory = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,dataSpinnerCategory)
        val adapterSpSupplier = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,dataSpinnerSupplier)
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
            val idCapturado=supplierList.filter { it.name==selec }[0].id
            idSupplier=idCapturado

            Log.i("ITEMSEL","$selec")
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        // Crea una variable de tipo Calendar para guardar la fecha actual o la fecha por defecto
        val myCalendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            // Actualizar el valor de myCalendar con la fecha seleccionada
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)

            // Mostrar la fecha en el EditText usando un formato
            val myFormat = "yyyy/MM/dd" // El formato que quieras
            val sdf = SimpleDateFormat(myFormat, Locale.US) // Crear un objeto SimpleDateFormat
            val dateTime = dialogView.findViewById<EditText>(R.id.edtDueDate) // Obtener el EditText
            dateTime.setText(sdf.format(myCalendar.time)) // Mostrar la fecha formateada
        }
        // Obtener el EditText
        val dateTime = dialogView.findViewById<EditText>(R.id.edtDueDate)

        // Crear el listener del EditText usando setOnClickListener
        dateTime.setOnClickListener {
            // Crear y mostrar el DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(), // El contexto
                dateSetListener, // El listener que creaste
                myCalendar.get(Calendar.YEAR), // El año inicial
                myCalendar.get(Calendar.MONTH), // El mes inicial
                myCalendar.get(Calendar.DAY_OF_MONTH) // El día inicial
            )
            datePickerDialog.show()
        }

        //GUARDAR UN PRODUCTO
        saveButton.setOnClickListener {
            val selectedDate = myCalendar.time
            val nombre = dialogView.findViewById<EditText>(R.id.edtName).text.toString()
            val costo_unitarioStr = dialogView.findViewById<EditText>(R.id.edtUnitCost).text.toString()
            val cantidadStr = dialogView.findViewById<EditText>(R.id.edtAmount).text.toString()
            val costo_unitario = costo_unitarioStr.toDoubleOrNull() ?: 0.0
            val cantidad = cantidadStr.toDoubleOrNull() ?: 0.0
            val productRequest = ProductRequest(nombre, selectedDate, costo_unitario, cantidad, idCategory, idSupplier)
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
            .baseUrl("https://smartfood-421500.uc.r.appspot.com/")
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
                    showError()
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
                    searchAllCategoriesWithProducts()
                } else {
                    showError()
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
                    showError()
                }
            }
        }
    }
    private fun searchAllSupplier() {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                getRetrofit().create(APIServiceSupplier::class.java).getAllSuplier("supplier/all")
            val sup = call.body()
            //Variable donde esta la respuesta
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val supliers = sup ?: emptyList()
                    supplierList.clear()
                    supplierList.addAll(supliers)
                    adapter.notifyDataSetChanged()

                    dataSpinnerSupplier.clear()
                    dataSpinnerSupplier.addAll(supplierList.map { it.name })
                } else {
                    showError()
                }
            }
        }
    }
    private fun searchAllCategoriesWithProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceCategory::class.java)
                .getAllCategoriesWithProducts("product/categories")
            val sup = call.body()
            //Variable donde esta la respuesta
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val categories = sup ?: emptyList()
                    categoryListI.clear()
                    categoryListI.addAll(categories)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
            }
        }
    }
    private fun showError() {
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
    }
}
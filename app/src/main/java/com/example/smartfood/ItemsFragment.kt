package com.example.smartfood

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
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
import com.example.smartfood.network.RetrofitClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
    private val productList = mutableListOf<String>()
    private var idCategory=0
    private var idUnit=0

    val imageList = listOf(
        R.drawable.verduras,
        R.drawable.carne,
        R.drawable.cereal,
        R.drawable.suministros,
        R.drawable.fruta,
        R.drawable.pescado,
        R.drawable.frutos_secos,
        R.drawable.alimentos_liquidos,
        R.drawable.df,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemsBinding.inflate(inflater)
        binding.root.alpha = 0f
        binding.rcyView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemContainerAdapter(categoryListI, imageList)
        binding.rcyView.adapter = adapter

        searchAllCategoriesWithProduct()
        binding.fabAddCategory.setOnClickListener { openDialogAddNewCategory() }
        binding.floatingButton.setOnClickListener { openDialogAddNewProduct() }
        fadeInAnimation(binding.root)
        return binding.root
    }

    private fun openDialogAddNewCategory() {
        val dialogView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_add_category, null)

        searchAllCategories()
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar nueva categoría")
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_add_category).setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.edt_new_category).text.toString().lowercase()

            if (dataSpinnerCategory.any { it.lowercase() == nombre }) {
                Snackbar.make(it, "Categoría duplicada.", Snackbar.LENGTH_LONG).show()
            }else {
                val categoryRequest = CategoryRequest(nombre)
                addCategory(categoryRequest)
                alertDialog.dismiss()
            }
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
        searchAllProducts()
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
            val idCapturado=unitList.filter { it.name==selec }[0].id
            idUnit=idCapturado
            Log.i("ITEMSEL","$selec")
        }

        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar nuevo producto")
            .setView(dialogView)
            .create()

        saveNewProductButton.setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.edtName).text.toString().lowercase()

            if (productList.any { it.lowercase() == nombre }) {
                Snackbar.make(it, "Producto duplicado.", Snackbar.LENGTH_LONG).show()
            } else {
                val productRequest = ProductRequest(nombre, idCategory, idUnit)
                addProduct(productRequest)
                alertDialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun addCategory(categoryRequest: CategoryRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                RetrofitClient.instance.create(APIServiceCategory::class.java).addCategory(categoryRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    searchAllCategoriesWithProduct()
                    view?.let { Snackbar.make(it, "Su categoría fue agregada exitosamente.", Snackbar.LENGTH_LONG).show() }
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun addProduct(productRequest: ProductRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                RetrofitClient.instance.create(APIServiceProduct::class.java).addProduct(productRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    searchAllCategoriesWithProduct()
                    view?.let { Snackbar.make(it, "Se agrego su producto exitosamente.", Snackbar.LENGTH_LONG).show() }
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun searchAllProducts(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.instance.create(APIServiceProduct::class.java)
                .getAllProductsLittle("product/quantity")
            val sup = call.body()
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val products = sup ?: emptyList()
                    productList.clear()
                    productList.addAll(products.map { it.productName })
                } else {
                    showError(10)
                }
            }
        }
    }
    private fun searchAllCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.instance.create(APIServiceCategory::class.java)
                .getAllCategories("category/all")
            val sup = call.body()
            //Variable donde esta la respuesta
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val categories = sup ?: emptyList()
                    categoryList.clear()
                    categoryList.addAll(categories)
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
            val call = RetrofitClient.instance.create(APIServiceUnit::class.java)
                .getAllUnits("unit/all")
            val sup = call.body()
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    val units = sup ?: emptyList()
                    unitList.clear()
                    unitList.addAll(units)
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
                val call = RetrofitClient.instance.create(APIServiceCategory::class.java)
                    .getAllCategoriesWithProducts("category/products/quantity")
                val sup = call.body()
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        binding.progressCircular.visibility = View.GONE
                        if (call.isSuccessful) {
                            val categories = sup ?: emptyList()
                            categoryListI.clear()
                            categoryListI.addAll(categories)
                            adapter.notifyDataSetChanged()
                        } else {
                            showError(10)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        binding.progressCircular.visibility = View.GONE
                        if (retryCount < 3) {
                            delay(2000)
                            searchAllCategoriesWithProduct(retryCount + 1)
                        } else {
                            showError(10)
                        }
                    }
                }
            }
        }
    }
    private suspend fun showError(retryCount: Int) {
        if (retryCount >= 3) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error en la conexión revise su red.", Toast.LENGTH_LONG).show()
            }
        } else {
            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.VISIBLE
            }
            delay(2000)
            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.GONE
                searchAllCategoriesWithProduct(retryCount + 1)
            }
        }
    }
    private fun fadeInAnimation(view: View) {
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.duration = 1500
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.start()
    }
}
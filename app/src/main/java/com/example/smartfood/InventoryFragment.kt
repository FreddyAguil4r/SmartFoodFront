package com.example.smartfood

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.InventoryAdapter
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.ModelResponse.UnitResponse
import com.example.smartfood.Request.PurchaseRequest
import com.example.smartfood.Request.SubstractProductRequest
import com.example.smartfood.Request.UpdateProductRequest
import com.example.smartfood.Service.APIServiceProduct
import com.example.smartfood.Service.APIServicePurchase
import com.example.smartfood.Service.APIServiceSupplier
import com.example.smartfood.Service.APIServiceUnit
import com.example.smartfood.databinding.FragmentInventoryBinding
import com.example.smartfood.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryFragment : Fragment() {
    private lateinit var binding: FragmentInventoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private val productList =  MutableLiveData<List<ProductResponse>>()

    private val supplierList = mutableListOf<SupplierResponse>()
    private val unitList = mutableListOf<UnitResponse>()
    private val dataSpinnerSupplier = mutableListOf<String>()
    private val dataSpinnerUnits = mutableListOf<String>()
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater)
        createNotificationChannel()
        recyclerView = binding.inventoryrcv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = InventoryAdapter(emptyList(),::deleteProducts,::updateProducts,
            ::makePurchase,::substractPurchase,dataSpinnerSupplier,supplierList,dataSpinnerUnits,unitList)
        recyclerView.adapter = adapter

        productList.observe(viewLifecycleOwner){products ->
            adapter.updateList(products)
        }

        searchAllProducts()
        searchAllSupplier()
        searchAllUnits()

        searchView = binding.searchViewInventory
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.updateList(productList.value ?: emptyList())
                } else {
                    newText.let {
                        adapter.filter.filter(it)
                    }
                }
                return false
            }
        })

        return binding.root
    }
    private fun searchAllProducts(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.instance.create(APIServiceProduct::class.java).getAllProducts("product/quantity")
                val sup = call.body()
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        if (call.isSuccessful) {
                            val products = sup ?: emptyList()
                            productList.postValue(products)
                        } else {
                            showErrorConnection(10)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        if (retryCount < 3) {
                            delay(2000)
                            searchAllProducts(retryCount + 1)
                        } else {
                            withContext(Dispatchers.Main) {
                                showErrorConnection(retryCount)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun deleteProducts(productId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.instance.create(APIServiceProduct::class.java).deleteProduct(productId)
            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    searchAllProducts()
                }else{
                    showError()
                }
            }
        }
    }
    private fun updateProducts(productId: Int, productRequest: UpdateProductRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = RetrofitClient.instance.create(APIServiceProduct::class.java)
            val response = apiService.updateProduct(productId, productRequest)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    searchAllProducts()
                } else {
                    showError()
                }
            }
        }
    }
    private fun searchAllSupplier() {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                RetrofitClient.instance.create(APIServiceSupplier::class.java).getAllSuplier("supplier/all")
            val sup = call.body()

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
                    adapter.notifyDataSetChanged()

                    dataSpinnerUnits.clear()
                    dataSpinnerUnits.addAll(unitList.map { it.name })
                } else {
                    showError()
                }
            }
        }
    }
    private fun makePurchase(purchaseRequest: PurchaseRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                RetrofitClient.instance.create(APIServicePurchase::class.java).makePurchaseProduct(purchaseRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    showSuccessfulMakePurchase()
                    searchAllProducts()
                } else {
                    showError()
                }
            }
        }
    }
    private fun substractPurchase(substractRequest: SubstractProductRequest){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = RetrofitClient.instance.create(APIServiceProduct::class.java)
                val response = apiService.substractProduct(substractRequest)
                if (response.isSuccessful) {
                    searchAllProducts()
                } else {
                    showError()
                }
            } catch (e: Exception) {
                showError()
            }
        }
    }
    private fun showSuccessfulMakePurchase(){
        Toast.makeText(requireContext(),"Se añadio la compra exitosamente", Toast.LENGTH_SHORT).show()
    }
    private fun showError() {
        Toast.makeText(requireContext(),"Error", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorConnection(retryCount: Int = 0) {
        if (retryCount >= 3) {
            Toast.makeText(requireContext(), "Error en la conexión revise su red.", Toast.LENGTH_LONG).show()
        } else {
            val progressDialog = ProgressDialog(requireContext()).apply {
                setTitle("Cargando")
                setMessage("Intentando reconectar...")
                setCancelable(false) // para que no se pueda cancelar
            }
            progressDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                delay(2000) // Espera antes de ocultar el diálogo
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    searchAllProducts(retryCount + 1)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    companion object {
        // ID único para el canal de notificaciones
        const val CHANNEL_ID = "inventory_channel"
    }
}
package com.example.smartfood

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.InventoryAdapter
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.Request.UpdateProductRequest
import com.example.smartfood.Service.APIServiceProduct
import com.example.smartfood.databinding.FragmentInventoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryFragment : Fragment() {
    private lateinit var binding: FragmentInventoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private val productList = mutableListOf<ProductResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater)
        createNotificationChannel()
        recyclerView = binding.inventoryrcv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = InventoryAdapter(productList,::deleteProducts,::updateProducts)
        recyclerView.adapter = adapter

        searchAllProducts()
        return binding.root
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://smartfood-421500.uc.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun searchAllProducts(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceProduct::class.java).getAllProducts("product/all")
            val sup = call.body()
            //Variable donde esta la respuesta
            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    val products = sup?: emptyList()
                    productList.clear()
                    productList.addAll(products)
                    adapter.notifyDataSetChanged()
                }else{
                    showError()
                }
            }
        }
    }

    private fun deleteProducts(productId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceProduct::class.java).deleteProduct(productId)
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
            val apiService = getRetrofit().create(APIServiceProduct::class.java)
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
    private fun showError() {
        Toast.makeText(requireContext(),"Error", Toast.LENGTH_SHORT).show()
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
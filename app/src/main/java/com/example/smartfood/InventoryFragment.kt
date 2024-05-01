package com.example.smartfood

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.InventoryAdapter
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.Request.ProductRequest
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
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(inflater)

        // Configura el RecyclerView y el adaptador
        recyclerView = binding.inventoryrcv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = InventoryAdapter(productList,::deleteProducts,::updateProducts)
        recyclerView.adapter = adapter

        searchAllProducts()

        //FloatActingButton
        //binding.floatingButton.setOnClickListener{openDialog()}

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

    suspend fun updateProducts(productId: Int, productRequest: UpdateProductRequest) {
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

    private fun openDialog() {
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_inventory, null)

        // Encuentra los Spinners en el diseño del dialogoseekBar
        val spinner1: Spinner = dialogView.findViewById(R.id.spn_name)
        val skeebar : SeekBar = dialogView.findViewById(R.id.seekbar)
        val cancelButton : Button = dialogView.findViewById(R.id.btnCancelInvetory)
        val saveButton : Button = dialogView.findViewById(R.id.btnSaveInventory)

        // Crea un AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        // Crea el AlertDialog
        val alertDialog = builder.create()

        //GUARDAR Y CANCELAR PRODUCTO
        saveButton.setOnClickListener {

        }
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // Muestra el AlertDialog
        alertDialog.show()
    }
    private fun showError() {
        Toast.makeText(requireContext(),"Error", Toast.LENGTH_SHORT).show()
    }
}
package com.example.smartfood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.TrendingAdapter
import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.ModelResponse.InventoryResponse
import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.Service.APIServiceCategory
import com.example.smartfood.Service.APIServiceProduct
import com.example.smartfood.Service.APIServiceTrending
import com.example.smartfood.databinding.FragmentTrendingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrendingFragment : Fragment() {
    private lateinit var binding: FragmentTrendingBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrendingAdapter
    private val categoryList = mutableListOf<CategoryResponse>()
    private val productList = mutableListOf<ProductResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrendingBinding.inflate(inflater)
        recyclerView = binding.rcvAllCategories
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = TrendingAdapter(categoryList)
        recyclerView.adapter = adapter

        searchInventory()
        searchAllCategories()
        return binding.root
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://26.54.240.231:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //FUNCION PARA LISTAR TODAS LAS CATEGORIAS
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

                } else {
                    showError()
                }
            }
        }
    }

    private fun searchInventory() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceProduct::class.java).getAllProducts("product/quantity")
            val sup = call.body()

            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    val products = sup?: emptyList()
                    productList.clear()
                    productList.addAll(products)
                    adapter.notifyDataSetChanged()
                    // Calcula el total del inventario
                    val totalInventario = productList.sumOf { it.totalInventory }

                    // Actualiza el TextView con el total del inventario
                    binding.txtTotalInventary.text = getString(R.string.total_inventory_format, totalInventario)
                }else{
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
    }
}






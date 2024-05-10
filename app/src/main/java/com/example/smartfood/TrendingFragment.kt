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
import com.example.smartfood.Service.APIServiceCategory
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
    var totalInventory: Double = 0.0

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
            try {
                val call = getRetrofit().create(APIServiceTrending::class.java)
                    .getInventory("inventory")

                // Verificar si la llamada fue exitosa
                if (call.isSuccessful) {
                    // Obtener la respuesta del cuerpo de la llamada
                    val inventoryResponse: InventoryResponse? = call.body()

                    // Verificar que la respuesta no sea nula
                    if (inventoryResponse != null) {
                        totalInventory = inventoryResponse.totalInventory
                        withContext(Dispatchers.Main) {
                            binding.txtTotalInventary.text = totalInventory.toString()
                        }
                    }
                } else {
                    // Manejar el caso en que la llamada no fue exitosa
                    withContext(Dispatchers.Main) {
                        showError()
                    }
                }
            } catch (e: Exception) {
                // Manejar excepciones, por ejemplo, conexión fallida
                withContext(Dispatchers.Main) {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
    }
}






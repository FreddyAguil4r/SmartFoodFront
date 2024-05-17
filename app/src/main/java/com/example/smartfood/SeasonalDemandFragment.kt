package com.example.smartfood

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.SeasonalDemandAdapter
import com.example.smartfood.ModelResponse.ProductResponseBigQuery
import com.example.smartfood.Service.APIServiceBigQuery
import com.example.smartfood.databinding.FragmentSeasonalDemandBinding
import com.example.smartfood.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeasonalDemandFragment : Fragment() {
    private lateinit var binding : FragmentSeasonalDemandBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : SeasonalDemandAdapter
    private var productListWithForecastSeasonal = mutableListOf<ProductResponseBigQuery>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeasonalDemandBinding.inflate(inflater)
        recyclerView = binding.recyclerViewSeasonalDemand
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SeasonalDemandAdapter(productListWithForecastSeasonal)
        recyclerView.adapter = adapter
        callStoreProcedureSeasonal()

        return binding.root
    }

    private fun callStoreProcedureSeasonal(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).callSp("/bigquery/callspbimensual")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Llamada exitosa.", Toast.LENGTH_LONG).show()
                        getAllProductsWithDemandBq()
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                if (retryCount < 3) {
                    delay(2000)
                    callStoreProcedureSeasonal(retryCount + 1)
                } else {
                    withContext(Dispatchers.Main) {
                        showError(10)
                    }
                }
            }
        }
    }

    private fun getAllProductsWithDemandBq(retryCount: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).getAllProductsWithDemand("/bigquery/bimensual")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val productListBq = response.body() ?: emptyList()
                        productListWithForecastSeasonal.clear()
                        productListWithForecastSeasonal.addAll(productListBq)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Datos recibidos con éxito.", Toast.LENGTH_LONG).show()
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                if (retryCount  < 3) {
                    delay(2000)
                    getAllProductsWithDemandBq(retryCount + 1)
                } else {
                    withContext(Dispatchers.Main) {
                        showError(10)
                    }
                }
            }
        }
    }

    private fun showError(retryCount: Int = 0) {
        if (retryCount >= 3) {
            Toast.makeText(requireContext(), "Error en la conexión. Revise su red.", Toast.LENGTH_LONG).show()
        } else {
            // Muestra un diálogo de progreso aquí
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
                    callStoreProcedureSeasonal(retryCount + 1)
                }
            }
        }
    }
}
package com.example.smartfood

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.MonthlyDemandAdapter
import com.example.smartfood.ModelResponse.ProductResponseBigQuery
import com.example.smartfood.Service.APIServiceBigQuery
import com.example.smartfood.databinding.FragmentMonthlyDemandBinding
import com.example.smartfood.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MonthlyDemandFragment : Fragment() {
    private lateinit var binding : FragmentMonthlyDemandBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MonthlyDemandAdapter
    private var productListWithForecast = mutableListOf<ProductResponseBigQuery>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMonthlyDemandBinding.inflate(inflater)
        recyclerView = binding.recyclerViewMonthlyDemand
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MonthlyDemandAdapter(productListWithForecast)
        recyclerView.adapter = adapter

        CallStoreProcedure()
        binding.btnSeasonalForecast.setOnClickListener {
            findNavController().navigate(
                MonthlyDemandFragmentDirections.actionMonthlyDemandFragmentToSeasonalDemandFragment()
            )
        }

        return binding.root
    }

    private fun CallStoreProcedure(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).callSp("/bigquery/callsp")
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
                    CallStoreProcedure(retryCount + 1)
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
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).getAllProductsWithDemand("/bigquery/mensual")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val productListBq = response.body() ?: emptyList()
                        productListWithForecast.clear()
                        productListWithForecast.addAll(productListBq)
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
            if (isAdded) {
                Toast.makeText(requireContext(), "Error en la conexión. Revise su red.", Toast.LENGTH_LONG).show()
            }
        } else {
            val progressDialog = ProgressDialog(requireContext()).apply {
                setTitle("Cargando")
                setMessage("Intentando reconectar...")
                setCancelable(false)
            }
            if (isAdded) {
                progressDialog.show()
            }
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                if (isAdded) {
                    progressDialog.dismiss()
                    CallStoreProcedure(retryCount + 1)
                }
            }
        }
    }
}
package com.example.smartfood.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.SortedAdapter
import com.example.smartfood.ModelResponse.MonthlyDemand
import com.example.smartfood.ModelResponse.ProductDemand
import com.example.smartfood.MonthlyDemandFragmentDirections
import com.example.smartfood.R
import com.example.smartfood.Service.APIServiceBigQuery
import com.example.smartfood.databinding.FragmentSortedBinding
import com.example.smartfood.network.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException


class SortedFragment : Fragment() {
    private lateinit var binding: FragmentSortedBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : SortedAdapter
    private var productListSorted = mutableListOf<MonthlyDemand>()
    private var productListSortedDemand = mutableListOf<ProductDemand>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSortedBinding.inflate(inflater)
        recyclerView = binding.recyclerViewMonthlyDemand
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SortedAdapter(productListSortedDemand)
        recyclerView.adapter = adapter

        CallStoreProcedure()
        binding.fabSeasonalForecast.setOnClickListener {
            findNavController().navigate(
                SortedFragmentDirections.actionSortedFragmentToSeasonalDemandFragment2()
            )
        }
        return binding.root
    }

    private fun setupMonthSpinner(monthlyDemands: List<MonthlyDemand>) {
        val months = monthlyDemands.map { it.mes }
        val monthSpinner = binding.root.findViewById<AutoCompleteTextView>(R.id.autoCompleteMonth)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months)
        monthSpinner.setAdapter(adapter)
        monthSpinner.setOnItemClickListener { parent, _, position, _ ->
            val selectedMonth = parent.getItemAtPosition(position) as String
            updateProductList(selectedMonth, monthlyDemands)
        }
    }

    private fun updateProductList(selectedMonth: String, monthlyDemands: List<MonthlyDemand>) {
        val selectedMonthlyDemand = monthlyDemands.find { it.mes == selectedMonth }
        selectedMonthlyDemand?.let {
            productListSortedDemand.clear()
            productListSortedDemand.addAll(it.data)
            adapter.notifyDataSetChanged()
        }
    }

    private fun CallStoreProcedure(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).callSp("/bigquery/callsp")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Snackbar.make(requireView(), "Llamada exitosa.", Snackbar.LENGTH_SHORT).show()
                        getAllProductsSortedWithDemandBq()
                    } else {
                        showError()
                    }
                }
            } catch (e: SocketTimeoutException) {
                if (retryCount < 3) {
                    delay(2000)
                    CallStoreProcedure(retryCount + 1)
                } else {
                    withContext(Dispatchers.Main) {
                        showError(10)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError()
                }
            }
        }
    }

    private fun getAllProductsSortedWithDemandBq(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.create(APIServiceBigQuery::class.java).getAllProductsSorted("/bigquery/formated")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val productListBq = response.body() ?: emptyList()
                        productListSorted.clear()
                        productListSorted.addAll(productListBq) // Asumiendo que response.body() devuelve List<MonthlyDemand>
                        setupMonthSpinner(productListSorted)
                        adapter.notifyDataSetChanged()
                        Snackbar.make(requireView(), "Cargando data de pronostico de demanda.", Snackbar.LENGTH_SHORT).show()
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                if (retryCount < 3) {
                    delay(2000)
                    getAllProductsSortedWithDemandBq(retryCount + 1)
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
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000) // Espera antes de intentar de nuevo
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    CallStoreProcedure(retryCount + 1)
                }
            }
        }
    }
}
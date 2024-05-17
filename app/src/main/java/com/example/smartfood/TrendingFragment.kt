package com.example.smartfood

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.TrendingAdapter
import com.example.smartfood.ModelResponse.CategoryTotal
import com.example.smartfood.ModelResponse.MonitorResponse
import com.example.smartfood.Service.APIServiceTrending
import com.example.smartfood.databinding.FragmentTrendingBinding
import com.example.smartfood.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendingFragment : Fragment() {
    private lateinit var binding: FragmentTrendingBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrendingAdapter
    private lateinit var monitorResponse: MonitorResponse
    private var categoryList = mutableListOf<CategoryTotal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrendingBinding.inflate(inflater)
        recyclerView = binding.rcvAllCategories
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        TotalValuesMonitor()
        adapter = TrendingAdapter(categoryList)
        recyclerView.adapter = adapter

        binding.btnLevels.setOnClickListener {
            val intent = Intent(requireContext(),SecondaryActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun TotalValuesMonitor(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.instance.create(APIServiceTrending::class.java).getTotalMonitor("category/quantity")
                val response = call.body()

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        monitorResponse = response ?: return@withContext
                        binding.txtTotalInventary.text = monitorResponse.totalInventario.toString()
                        categoryList.addAll(monitorResponse.categories)
                        adapter.notifyDataSetChanged()
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                if (retryCount < 3) {
                    delay(2000)
                    TotalValuesMonitor(retryCount + 1)
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
                    TotalValuesMonitor(retryCount + 1)
                }
            }
        }
    }
}






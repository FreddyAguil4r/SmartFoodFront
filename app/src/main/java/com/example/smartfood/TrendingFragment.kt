package com.example.smartfood

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

        return binding.root
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://26.54.240.231:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun TotalValuesMonitor() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceTrending::class.java).getTotalMonitor("category/quantity")
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
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
    }
}






package com.example.smartfood.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.SortedAdapter
import com.example.smartfood.ModelResponse.ProductDemand
import com.example.smartfood.R
import com.example.smartfood.databinding.FragmentSortedBinding


class SortedFragment : Fragment() {
    private lateinit var binding: FragmentSortedBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : SortedAdapter
    private var productSortedList = mutableListOf<ProductDemand>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSortedBinding.inflate(inflater)
        recyclerView = binding.recyclerViewMonthlyDemand
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SortedAdapter(productSortedList)
        recyclerView.adapter = adapter
        return binding.root
    }
}
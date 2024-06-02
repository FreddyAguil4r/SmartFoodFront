package com.example.smartfood

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfood.Adapter.SupplierAdapter
import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.Request.SupplierRequest
import com.example.smartfood.Service.APIServiceSupplier
import com.example.smartfood.databinding.FragmentSupplierBinding
import com.example.smartfood.network.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SupplierFragment : Fragment() {
    private lateinit var binding: FragmentSupplierBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupplierAdapter
    private val supplierList = MutableLiveData<List<SupplierResponse>>()
    private lateinit var searchView: SearchView

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSupplierBinding.inflate(inflater)

       recyclerView = binding.rcyViewSupplier
       recyclerView.layoutManager = LinearLayoutManager(requireContext())
       adapter = SupplierAdapter(emptyList(),::deleteSupplier,::updateSuppliers)
       recyclerView.adapter = adapter

       searchAllSupplier()
       supplierList.observe(viewLifecycleOwner) { suppliers ->
           adapter.updateList(suppliers)
       }


       searchView = binding.searchView
       searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }
           override fun onQueryTextChange(newText: String?): Boolean {
               if (newText.isNullOrEmpty()) {
                   adapter.updateList(supplierList.value ?: emptyList())
               } else {
                   newText.let {
                       adapter.filter.filter(it)
                   }
               }
               return false
           }
       })

       binding.floatingButton.setOnClickListener{addNewSupplierDialog()}
       return binding.root
   }

    private fun addNewSupplierDialog() {
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_supplier, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.editTextNombre).text.toString()
            val ruc = dialogView.findViewById<EditText>(R.id.editTextRUC).text.toString()
            val direccion = dialogView.findViewById<EditText>(R.id.editTextDireccion).text.toString()
            val supplierRequest = SupplierRequest(nombre, ruc, direccion)
            addSupplier(supplierRequest)
            alertDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    private fun searchAllSupplier(retryCount: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.instance.create(APIServiceSupplier::class.java).getAllSuplier("supplier/all")
                val sup = call.body()
                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val suppliers = sup ?: emptyList()
                        supplierList.postValue(suppliers)
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                if (retryCount < 3) {
                    delay(2000)
                    searchAllSupplier(retryCount + 1)
                } else {
                    withContext(Dispatchers.Main) {
                        showError()
                    }
                }
            }
        }
    }
    private fun addSupplier(supplierRequest: SupplierRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.instance.create(APIServiceSupplier::class.java).addSupplier(supplierRequest)
            withContext(Dispatchers.Main) {
                if (call.isSuccessful) {
                    searchAllSupplier()
                } else {
                    showError()
                }
            }
        }
    }
    suspend fun updateSuppliers(supplierId: Int, supplierRequest: SupplierRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = RetrofitClient.instance.create(APIServiceSupplier::class.java)
            val response = apiService.updateSupplier(supplierId, supplierRequest)  // Call editSupplier method
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    // Update UI with edited supplier data (potentially reload data)
                    searchAllSupplier() // Assuming searchAllSupplier reloads data
                } else {
                    showError() // Handle API errors
                }
            }
        }
    }
    private fun deleteSupplier(supplierId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.instance.create(APIServiceSupplier::class.java).deleteSupplier(supplierId)
            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    searchAllSupplier()
                }else{
                    showErrorDelete()
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
                setCancelable(true) // Permitir que se pueda cancelar
            }
            progressDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                delay(2000) // Espera antes de intentar reconectar
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    searchAllSupplier(retryCount + 1)
                }
            }
        }
    }

    private fun showErrorDelete() {
        Snackbar.make(requireView(), "No se puede eliminar este proveedor porque tiene productos asociados.", Snackbar.LENGTH_SHORT).show()
    }
}
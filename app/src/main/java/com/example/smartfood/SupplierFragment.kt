package com.example.smartfood

import android.app.AlertDialog
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

       supplierList.observe(viewLifecycleOwner) { suppliers ->
           adapter.updateList(suppliers)
       }
       searchAllSupplier()

       searchView = binding.searchView
       searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }
           override fun onQueryTextChange(newText: String?): Boolean {
               newText?.let {
                   // Filtra la lista de proveedores basado en el texto ingresado
                   adapter.filter.filter(it)
               }
               return false
           }
       })

       binding.floatingButton.setOnClickListener{addNewSupplierDialog()}
       return binding.root
   }

    private fun addNewSupplierDialog() {
        // Infla el layout del Custom Dialog
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout_supplier, null)

        // Crea un AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        // Crea el AlertDialog
        val alertDialog = builder.create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val nombre = dialogView.findViewById<EditText>(R.id.editTextNombre).text.toString()
            val ruc = dialogView.findViewById<EditText>(R.id.editTextRUC).text.toString()
            val direccion = dialogView.findViewById<EditText>(R.id.editTextDireccion).text.toString()
            val supplierRequest = SupplierRequest(nombre, ruc, direccion)
            addSupplier(supplierRequest)
            alertDialog.dismiss() // Cerrar el diálogo después de agregar el proveedor.
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        // Muestra el AlertDialog
        alertDialog.show()
    }
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://26.54.240.231:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun searchAllSupplier(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceSupplier::class.java).getAllSuplier("supplier/all")
            val sup = call.body()
            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    //?: En caso de que sea nulo, se asigna el valor de la derecha.
                    val suppliers = sup?: emptyList()
                    supplierList.postValue(suppliers)
                }else{
                    showError()
                }
            }
        }
    }
    private fun addSupplier(supplierRequest: SupplierRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIServiceSupplier::class.java).addSupplier(supplierRequest)
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
            val apiService = getRetrofit().create(APIServiceSupplier::class.java)
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
            val call = getRetrofit().create(APIServiceSupplier::class.java).deleteSupplier(supplierId)
            withContext(Dispatchers.Main){
                if(call.isSuccessful){
                    searchAllSupplier()
                }else{
                    showErrorDelete()
                }
            }
        }
    }
    private fun showError() {
        Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
    }
    private fun showErrorDelete() {
        Toast.makeText(requireContext(),"Error delete method",Toast.LENGTH_SHORT).show()
    }
}
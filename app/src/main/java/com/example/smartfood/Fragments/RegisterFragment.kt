package com.example.smartfood.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.smartfood.Request.UserRequest
import com.example.smartfood.Service.APIServiceUser
import com.example.smartfood.databinding.FragmentRegisterBinding
import com.example.smartfood.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)


        binding.btnRegisterUser.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPasswordRegister.text.toString()
            val name = binding.etName.text.toString()
            val lastname = binding.etlastname.text.toString()
            val newUser = UserRequest(email, password, name, lastname)
            addNewUser(newUser)
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
        return binding.root
    }

    private fun addNewUser(user : UserRequest){
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                RetrofitClient.instance.create(APIServiceUser::class.java).registerUser(user)
            withContext(Dispatchers.Main){
                if (call.isSuccessful) {
                    Toast.makeText(requireContext(), "Usuario registrado exitosamente.", Toast.LENGTH_LONG).show()
                } else {
                    showError(10)
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
                }
            }
        }
    }
}
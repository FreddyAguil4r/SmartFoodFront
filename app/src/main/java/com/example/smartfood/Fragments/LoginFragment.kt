package com.example.smartfood.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.smartfood.Request.LoginRequest
import com.example.smartfood.Service.APIServiceUser
import com.example.smartfood.databinding.FragmentLoginBinding
import com.example.smartfood.network.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etUser.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Snackbar.make(view, "Por favor ingrese los campos solicitados", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }
    private fun loginUser(email: String, password: String) {
        val userRequest = LoginRequest(email, password)

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.instance.create(APIServiceUser::class.java).login(userRequest)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    Snackbar.make(requireView(), "Login exitoso", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
                } else {
                    Snackbar.make(requireView(), "Error al ingresar las credenciales", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
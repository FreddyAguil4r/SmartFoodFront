package com.example.smartfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.smartfood.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private fun setTitleBasedOnFragment(fragment: Fragment) {
        when (fragment) {
            is ItemsFragment -> supportActionBar?.title = "Elementos"
            is InventoryFragment -> supportActionBar?.title = "Inventario"
            is TrendingFragment -> supportActionBar?.title = "Monitor"
            is SupplierFragment -> supportActionBar?.title = "Proveedores"
            else -> supportActionBar?.title = "SmartFood"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ItemsFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.items-> replaceFragment(ItemsFragment())
                R.id.inventory-> replaceFragment(InventoryFragment())
                R.id.trending-> replaceFragment(TrendingFragment())
                R.id.supplier-> replaceFragment(SupplierFragment())
                else->  {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
        setTitleBasedOnFragment(fragment)
    }
}
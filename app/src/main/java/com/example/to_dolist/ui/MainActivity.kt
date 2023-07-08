package com.example.to_dolist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("GiaHuy", "onCreateActivity")

        setSupportActionBar(binding.toolBar)
        configNavigation()

        binding.ivOpenDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navView)
        }

//        binding.navView.setNavigationItemSelectedListener { menuItem ->
//            when(menuItem.itemId) {
//                R.id.starTaskFragment -> {
//                    binding.toolBar.visibility = View.VISIBLE
//                    binding.layoutBottom.visibility = View.GONE
//                    true
//                }
//                else -> false
//            }
//        }


    }


    override fun onStart() {
        super.onStart()
        Log.e("GiaHuy", "onStart Activity")
    }

    override fun onResume() {
        super.onResume()
        Log.e("GiaHuy", "onResume Activity")
    }

    override fun onPause() {
        super.onPause()
        Log.e("GiaHuy", "onPause Activity")
    }

    override fun onStop() {
        super.onStop()
        Log.e("GiaHuy", "onStop Activity")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) ||super.onSupportNavigateUp()
    }

    private fun configNavigation() {
        val drawerLayout = binding.drawerLayout
        val navView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigation = binding.bottomNavigation
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.taskFragment,
                R.id.calendarFragment,
                R.id.meFragment
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigation.setupWithNavController(navController)
        navView.setupWithNavController(navController)
    }
}
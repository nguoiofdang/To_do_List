package com.example.to_dolist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityMainBinding
import com.example.to_dolist.db.Database
import com.example.to_dolist.repository.Repository
import com.example.to_dolist.viewmodel.TaskViewModel
import com.example.to_dolist.viewmodel.TaskViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Create ViewModel
        val repository = Repository(Database.invoke(this))
        val taskViewModelProviderFactory = TaskViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, taskViewModelProviderFactory)[TaskViewModel::class.java]

        //Setup Toolbar, BottomNavigation, NavigationView
        setSupportActionBar(binding.toolBar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout
        configNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun configNavigation() {
        val navView = binding.navView
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
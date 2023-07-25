package com.example.to_dolist.ui

import android.content.Intent
import android.os.Build
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
import com.example.to_dolist.models.Task
import com.example.to_dolist.repository.Repository
import com.example.to_dolist.services.NotificationService
import com.example.to_dolist.utils.Constance.TAG_INTENT_TASK
import com.example.to_dolist.viewmodel.TaskViewModel
import com.example.to_dolist.viewmodel.TaskViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    lateinit var viewModel: TaskViewModel
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startNotificationService()

        //Create ViewModel
        val repository = Repository(Database.invoke(this))
        val taskViewModelProviderFactory = TaskViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, taskViewModelProviderFactory)[TaskViewModel::class.java]

        //Setup Toolbar, BottomNavigation, NavigationView
        setSupportActionBar(binding.toolBar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        if (intent.hasExtra(TAG_INTENT_TASK)) {
            bundle = Bundle()
            val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(TAG_INTENT_TASK, Task::class.java)!!
            } else {
                intent.getSerializableExtra(TAG_INTENT_TASK) as Task
            }
            bundle?.putSerializable(TAG_INTENT_TASK, task)
            Log.e("pass", "intent activity")
            Log.e("pass", "intent activity Task=$task")
            Log.e("pass", "intent activity Bundle=$bundle")
        }
        navController.setGraph(R.navigation.nav_graph, bundle)
        drawerLayout = binding.drawerLayout
        configNavigation()
        Log.e("pass", "onCreate activity")
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

    private fun startNotificationService() {
        val intent = Intent(this, NotificationService::class.java)
        startService(intent)
    }

}
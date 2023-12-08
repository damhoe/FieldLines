package com.damhoe.fieldlines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.fieldlines.R
import com.example.fieldlines.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Set as support action bar for menus
        setSupportActionBar(binding.toolbar)

        // Setup navigation controller
        val navController = findNavController()
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
    }

    private fun findNavController() = findNavController(this, R.id.nav_host_fragment)
}
package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.databinding.ActivityMainBinding

@OptIn(NavigationUiSaveStateControl::class)
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(navView, navController, false)
    }

    fun getBottomBaseline() = binding.bottomBaseline

    fun getTopBaseline() = binding.topBaseline
}
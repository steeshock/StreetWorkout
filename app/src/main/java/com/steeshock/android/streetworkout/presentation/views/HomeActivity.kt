package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.steeshock.android.streetworkout.R

@OptIn(NavigationUiSaveStateControl::class)
class HomeActivity : AppCompatActivity() {

    var baseline: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseline = findViewById(R.id.baseline)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(navView, navController, false)
    }
}
package com.steeshock.streetworkout.presentation.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.databinding.ActivityMainBinding
import com.steeshock.streetworkout.services.permissions.PermissionsDelegate
import com.steeshock.streetworkout.services.permissions.PermissionsDelegateImpl

@OptIn(NavigationUiSaveStateControl::class)
class MainActivity : AppCompatActivity(), IBaseline,
    PermissionsDelegate by PermissionsDelegateImpl() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(navView, navController, false)

        registerPermissionDelegate(this)
    }

    override fun getBottomBaseline() = binding.bottomBaseline

    override fun getTopBaseline() = binding.topBaseline

    override fun getNavBarBaseline() = binding.navView
}

interface IBaseline {
    fun getBottomBaseline(): View?

    fun getTopBaseline(): View?

    fun getNavBarBaseline(): View?
}
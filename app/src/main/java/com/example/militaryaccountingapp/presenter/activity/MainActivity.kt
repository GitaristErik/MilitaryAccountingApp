package com.example.militaryaccountingapp.presenter.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)

        startThemeJob()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupNavController()
    }

    private fun startThemeJob() {
        lifecycleScope.launch {
            installSplashScreen()
            DynamicColors.applyToActivityIfAvailable(this@MainActivity)
        }
    }

    private fun setupNavController() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController() ?: return
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_history,
                R.id.navigation_statistics,
                R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)
    }

    private fun findNavController(): NavController? {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as? NavHostFragment

        return navHostFragment?.navController
    }
}


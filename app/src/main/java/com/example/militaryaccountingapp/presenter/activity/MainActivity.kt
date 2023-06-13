package com.example.militaryaccountingapp.presenter.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ActivityMainBinding
import com.example.militaryaccountingapp.presenter.shared.delegation.FabScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.internal.ToolbarUtils
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FabScreen {

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
        setupActionBar(navController)
        navView.setupWithNavController(navController)
        setupComponents(navController)
    }

    private fun setupActionBar(navController: NavController) {
        val helper = ComponentsHelper()
        helper.setToolbarStatusBar(R.color.md_surface)
        helper.setToolbarDrawableStart(R.drawable.flag_version4)

        setSupportActionBar(binding.toolbar)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setupActionBarWithNavController(
            navController, AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_history,
                    R.id.navigation_statistics,
                    R.id.navigation_profile
                )
            )
        )
    }

    private fun setupComponents(navController: NavController) {
        val componentsHelper = ComponentsHelper()

        componentsHelper.setupNavInsets()
        componentsHelper.setupFab(navController)
        componentsHelper.setSysInsets()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home) {
                componentsHelper.setToolbarDrawableStart(R.drawable.flag_version4)
                componentsHelper.showSearch()
                componentsHelper.showFab()
                // TODO: handle search bar and view
            } else {
                componentsHelper.setToolbarDrawableStart(0)
                componentsHelper.hideSearch()
                componentsHelper.hideFab()
                if (destination.id == R.id.itemFragment ||
                    destination.id == R.id.categoryFragment
                ) {
                    componentsHelper.showFab()
                }
            }


            if (destination.id in listOf(
                    R.id.navigation_home,
                    R.id.navigation_history,
                    R.id.navigation_statistics,
                    R.id.navigation_profile,
                )
            ) {
                componentsHelper.showActionBar()
                componentsHelper.showBottomNavigation()
            } else {
                componentsHelper.hideActionBar()
                componentsHelper.hideBottomNavigation()
            }
        }
    }

    private fun findNavController(): NavController? {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as? NavHostFragment

        return navHostFragment?.navController
    }

    override fun showFab() {
        ComponentsHelper().showFab()
    }

    override fun hideFab() {
        ComponentsHelper().hideFab()
    }

    override fun setOnClickFabListener(onFabClick: ((View) -> Unit)) {
        binding.fab.setOnClickListener {
            onFabClick(it)
        }
    }

    private inner class ComponentsHelper {
        @SuppressLint("RestrictedApi")
        fun setToolbarDrawableStart(drawableResId: Int, padding: Int? = null) {
            ToolbarUtils.getTitleTextView(binding.toolbar)?.let {
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableResId, 0, 0, 0)
                it.compoundDrawablePadding =
                    padding ?: resources.getDimensionPixelSize(R.dimen.padding_small_extra)
            }
        }

        fun setToolbarStatusBar(colorResId: Int) {
            binding.appBarLayout.statusBarForeground = MaterialShapeDrawable().apply {
                setTint(resources.getColor(colorResId, null))
            }
        }

        /**
         * Hide search bar and view with animation
         * @param duration - duration of animation
         */
        fun hideSearch(duration: Duration = Duration.ofMillis(DURATION_SEARCH)) {
            listOf(binding.searchBar, binding.searchView).forEach {
//                animateViews(it, 0f, duration)
                it.visibility = View.GONE
            }
        }

        /**
         * Show search bar and view with animation
         * @param duration - duration of animation
         */
        fun showSearch(duration: Duration = Duration.ofMillis(DURATION_SEARCH)) {
            listOf(binding.searchBar, binding.searchView).forEach {
                it.visibility = View.VISIBLE
//                animateViews(it, 1f, duration)
            }
        }

        private fun animateViews(view: View, alpha: Float, duration: Duration) {
            view.animate().alpha(alpha).duration = duration.toMillis()
        }

        fun hideActionBar() {
            supportActionBar?.hide()
        }

        fun showActionBar() {
            supportActionBar?.show()
        }

        fun hideBottomNavigation() {
            binding.navView.visibility = View.GONE
        }

        fun showBottomNavigation() {
            binding.navView.visibility = View.VISIBLE
        }

        fun hideFab() {
            binding.fab.hide()
        }

        fun showFab() {
            binding.fab.show()
        }

        fun setupFab(navController: NavController) {
            binding.fab.setOnClickListener {

            }
            setupFabInsets()
        }

        private var systemNavigationInsets: Int? = null
        private fun getSystemNavigationInsets(insets: WindowInsetsCompat): Int {
            if (systemNavigationInsets == null) {
                systemNavigationInsets = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                ).bottom / 2
            }
            return systemNavigationInsets!!
        }

        fun setupFabInsets() {
            ViewCompat.setOnApplyWindowInsetsListener(
                binding.fab
            ) { view, insets ->
                view.updatePadding(
                    bottom = getSystemNavigationInsets(insets)
                )
                insets
            }
        }

        fun setupNavInsets() = ViewCompat.setOnApplyWindowInsetsListener(
            binding.navView
        ) { view, insets ->
            view.updatePadding(
                bottom = getSystemNavigationInsets(insets)
            )
            insets
        }

        fun setSysInsets() {
            binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    companion object {
        private const val DURATION_SEARCH = 300L
    }
}


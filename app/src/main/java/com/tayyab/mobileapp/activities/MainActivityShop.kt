package com.tayyab.mobileapp.activities

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.databinding.ActivityMainShopBinding
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.AuthUtils


class MainActivityShop : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainShopBinding
    var appSettings: AppSettings?=null
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewModel: MainActivityShopViewModel
   // private val activityViewModel: MainActivityShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainActivityShop.toolbar)
        viewModel =
            ViewModelProvider(this).get(MainActivityShopViewModel::class.java)
        appSettings= AppSettings(applicationContext)
//        binding.appBarMainActivityShop.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_shop)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_categories, R.id.nav_products
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView= findViewById(R.id.bottom_navigation)
        //   setupActionBarWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.menu.removeItem(R.id.nav_logout)


        viewModel.getProductsStart().observe(this,
            { t ->
                val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_products)
                badge.isVisible = true
// An icon only badge will be displayed unless a number is set:
                badge.number = t
            })

        //sendRequest()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity_shop, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_shop)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        val authUtils=AuthUtils(applicationContext)
        viewModel.getProducts(authUtils.getUserID())
    }
}
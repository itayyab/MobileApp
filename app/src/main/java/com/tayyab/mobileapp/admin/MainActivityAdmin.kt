package com.tayyab.mobileapp.admin

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
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.databinding.ActivityMainAdminBinding
import com.tayyab.mobileapp.dialogs.CategoryBottomSheetDialog
import com.tayyab.mobileapp.dialogs.ProductBottomSheetDialog
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.AppSettings


class MainActivityAdmin : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewModel: MainActivityAdminViewModel
    private lateinit var appSettings: AppSettings


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this).get(MainActivityAdminViewModel::class.java)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainActivityAdmin.toolbar)
        appSettings = AppSettings(applicationContext)

        //   val navController = findNavController(R.id.nav_host_fragment_content_main_activity_admin)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        viewModel.getDialogStart().observe(this, { t ->
            if (t.product) {
                //launchCustomAlertDialogProducts(true, t.data)
                val bottomSheet = ProductBottomSheetDialog()
                bottomSheet.editProduct = true
                bottomSheet.product = t.data as Product
                bottomSheet.setViewModel(viewModel)
                bottomSheet.show(supportFragmentManager, "")
            } else {
//                launchCustomAlertDialogCats(true, t.data)
                val bottomSheet = CategoryBottomSheetDialog()
                bottomSheet.editProduct = true
                bottomSheet.category = t.data as Category
                bottomSheet.setViewModel(viewModel)
                bottomSheet.show(supportFragmentManager, "")
            }
        }
        )

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_categories, R.id.nav_products
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        //   setupActionBarWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.menu.removeItem(R.id.nav_shop)
        bottomNavigationView.menu.removeItem(R.id.nav_logout)


        binding.appBarMainActivityAdmin.fab.setOnClickListener {
            val id = navController.currentDestination!!.id
            if (id == R.id.nav_categories) {
                val bottomSheet = CategoryBottomSheetDialog()
                bottomSheet.editProduct = false
                bottomSheet.category = null
                bottomSheet.setViewModel(viewModel)
                bottomSheet.show(supportFragmentManager, "")

            } else if (id == R.id.nav_products) {
                val bottomSheet = ProductBottomSheetDialog()
                bottomSheet.editProduct = false
                bottomSheet.product = null
                bottomSheet.setViewModel(viewModel)
                bottomSheet.show(supportFragmentManager, "")

            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_admin)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity_shop, menu)
        return true
    }


}
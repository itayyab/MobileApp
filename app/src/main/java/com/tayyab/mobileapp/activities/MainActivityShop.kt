package com.tayyab.mobileapp.activities

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.tayyab.mobileapp.databinding.ActivityMainShopBinding
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.VolleySingleton
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R


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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView= findViewById(R.id.bottom_navigation)
        //   setupActionBarWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)


        viewModel!!.getProductsStart().observe(this,
            Observer<Int> { t ->
                var badge = bottomNavigationView.getOrCreateBadge(R.id.nav_gallery)
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
        viewModel!!.getProducts(getUser())
    }

    fun getUser():String{
        var extract= appSettings?.getToken()?.split(".")?.get(1)
        val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
        //Toast.makeText(context,decodedBytes.toString(), Toast.LENGTH_LONG).show()

        val decodedString = String(decodedBytes)
        val javaRootMapObject: Map<*, *> = Gson().fromJson(
            decodedString,
            Map::class.java
        )
        return javaRootMapObject.get("UserID").toString()
    }
    fun sendRequest(){


        // var userid=appSettings.g
//        var carx: CartDetails[] = [{ cD_Pr_id: productid, cD_id:0, cartForeignKey: 0, cD_Pr_Amnt: 0, cD_Pr_price: 0, cD_Pr_Qty: 0, cart: null, product: null, productForeignKey:0 }];
//        var cart = {
//            cart_id: 0, status: "PENDING", totalAmount: 0, totalQty: 0, userID: heroes, cartDetails: carx
//        } as Cart;

        var extract= appSettings?.getToken()?.split(".")?.get(1)
        val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
        //Toast.makeText(context,decodedBytes.toString(), Toast.LENGTH_LONG).show()

        val decodedString = String(decodedBytes)
        val javaRootMapObject: Map<*, *> = Gson().fromJson(
            decodedString,
            Map::class.java
        )
        Toast.makeText(applicationContext,decodedString.toString(), Toast.LENGTH_LONG).show()
            // var carxd: List<CartDetail> = listOf(CartDetail(0,1,product.pr_id,0,0,null,0,null,0))
//        var cart = Carts(carxd,0,"PENDING",0,0,javaRootMapObject.get("UserID").toString())
//
//        val jsonString = Gson().toJson(cart)
//        val jsonObject = JSONObject(jsonString)

//        val paramsx = HashMap<String,String>()
//        paramsx["username"] = "tayyab"
//        paramsx["password"] = "123456"
        //  val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : StringRequest(
            Request.Method.GET,
            Config.BASE_URL +"Carts/GetCartCount/"+javaRootMapObject.get("UserID").toString(),
            Response.Listener { response ->

//                var token=  response.get("token").toString()
//                var extract= token.split(".")[1]
//                val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
//                val decodedString = String(decodedBytes)
//                val javaRootMapObject: Map<*, *> = Gson().fromJson(
//                    decodedString,
//                    Map::class.java
//                )
                var badge = bottomNavigationView.getOrCreateBadge(R.id.nav_gallery)
                badge.isVisible = true
// An icon only badge will be displayed unless a number is set:
                badge.number = response.toInt()
                Toast.makeText(applicationContext,response.toString(), Toast.LENGTH_LONG).show()
                              //                appSettings?.saveLoggedIn(true)
//                appSettings?.saveToken(token)
//                appSettings?.saveIsAdmin(javaRootMapObject.get("UserID")!!.equals("ADMIN"))
//                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
//                //intent.putExtra("WID", obj.WID)
//                startActivity(intent)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext,error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("REEEEE:",error.message.toString())
            }
        ){
            override fun getHeaders(): Map<String, String>? {
                val headers: HashMap<String, String> = HashMap()
              //  headers["Authorization"] = "Basic $base64EncodedCredentials"
                headers["Authorization"] = "Bearer "+appSettings?.getToken()
                return headers
            }
        }



        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }
}
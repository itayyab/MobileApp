package com.tayyab.mobileapp.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.tayyab.mobileapp.databinding.ActivityMainAdminBinding
import com.tayyab.mobileapp.databinding.ProductsInputDialogBinding
import java.io.IOException

import android.graphics.Bitmap
import android.net.Uri

import android.util.Base64
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tayyab.mobileapp.Config
import java.util.*

import androidx.lifecycle.ViewModelProvider

import java.util.HashMap

import com.android.volley.AuthFailureError

import com.android.volley.VolleyError

import org.json.JSONObject

import com.android.volley.NetworkResponse
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.tayyab.mobileapp.activities.AuthActivity
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.databinding.CategoriesInputDialogBinding
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.utils.*

import org.json.JSONException
import java.net.HttpURLConnection

import com.tayyab.mobileapp.R


class MainActivityAdmin : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var customAlertDialogView: View
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var viewModel: MainActivityAdminViewModel
    private lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this).get(MainActivityAdminViewModel::class.java)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(binding.appBarMainActivityAdmin.toolbar)
        appSettings = AppSettings(applicationContext)
        //   val navController = findNavController(R.id.nav_host_fragment_content_main_activity_admin)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        viewModel.getDialogStart().observe(this, androidx.lifecycle.Observer { t ->
            if(t){
                launchCustomAlertDialogProducts()
            }else {
                launchCustomAlertDialogCats()
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
                R.id.nav_categories, R.id.nav_products, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        //   setupActionBarWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

        binding.appBarMainActivityAdmin.fab.setOnClickListener { view ->

//            launchCustomAlertDialog()
//            selectpic()

            val id = navController.currentDestination!!.id
            if (id == R.id.nav_categories) {
                launchCustomAlertDialogCats()
            } else if (id == R.id.nav_products) {
                launchCustomAlertDialogProducts()
            }
//            Snackbar.make(view, id.toString()+":"+R.id.nav_categories, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
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

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_shop)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    private fun supload(image: String, multipartBody: ByteArray) {
//
        val mimeType = "multipart/form-data"
        val jsonArrayRequest = object : StringRequest(
            Request.Method.POST,
            Config.BASE_URL + "Products/UploadFile",
            Response.Listener { response ->

//                var token=  response.get("token").toString()
//                var extract= token.split(".")[1]
//                val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
//                val decodedString = String(decodedBytes)
//                val javaRootMapObject: Map<*, *> = Gson().fromJson(
//                    decodedString,
//                    Map::class.java
//                )

                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                //                appSettings?.saveLoggedIn(true)
//                appSettings?.saveToken(token)
//                appSettings?.saveIsAdmin(javaRootMapObject.get("UserID")!!.equals("ADMIN"))
//                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
//                //intent.putExtra("WID", obj.WID)
//                startActivity(intent)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                    .show()
                Log.e("REEEEE:", error.message.toString())
            }
        ) {
            override fun getHeaders(): Map<String, String>? {
                val headers: HashMap<String, String> = HashMap()
                //  headers["Authorization"] = "Basic $base64EncodedCredentials"
                headers["Authorization"] = "Bearer " + appSettings?.getToken()
                return headers
            }

            override fun getBodyContentType(): String {
                return mimeType
            }

            //            override fun getBody(): ByteArray {
//                return multipartBody
//            }
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = Hashtable<String, String>()

                params["file"] = image
                return params
                //  return super.getParams()
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    private fun selectpic() {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(photoPickerIntent, REQUEST_CODE_PHOTOPICK)

        startForResult.launch(photoPickerIntent)
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val mImageUri: Uri? = intent!!.data

                val bmp = getBitmapFromUri(mImageUri!!)
                //  val image: String? = getStringImage(bmp!!)
                // Log.d("image", image)
                //passing the image to volley
                //SendImage(image)
                val baos = ByteArrayOutputStream()
                bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageBytes = baos.toByteArray()
                //val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                // supload(imageString,imageBytes)
                uploadBitmap(imageBytes)
                //   Toast.makeText(this, bmp.toString(), Toast.LENGTH_SHORT).show()
                // Handle the Intent
            }
        }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == REQUEST_CODE_PHOTOPICK && resultCode == RESULT_OK && data != null && data.data != null) {
             val filePath: Uri? = data.data
             try {
                 val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                 var lastBitmap: Bitmap? = null
                 lastBitmap = bitmap
                 //encoding image to string
                 val image: String? = getStringImage(lastBitmap)
                // Log.d("image", image)
                 //passing the image to volley
                 //SendImage(image)
             } catch (e: IOException) {
                 e.printStackTrace()
             }
         }
     }*/
    fun getStringImage(bmp: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun launchCustomAlertDialogProducts() {
        var productinputbinding: ProductsInputDialogBinding =
            ProductsInputDialogBinding.inflate(layoutInflater)

//        nameTextField = customAlertDialogView.findViewById(R.id.name_text_field)
//        phoneNumberTextField = customAlertDialogView.findViewById(R.id.phone_number_text_field)
//        addressTextField = customAlertDialogView.findViewById(R.id.address_text_field)
        customAlertDialogView = productinputbinding.root
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle("Add Product")

            .setPositiveButton("Add") { dialog, _ ->
//                val name = nameTextField.editText?.text.toString()
//                val phoneNumber = phoneNumberTextField.editText?.text.toString()
//                val address = addressTextField.editText?.text.toString()
//
//                val isInputValid : Boolean = validateInput(name, phoneNumber, address)
//
//                if (isInputValid) {
//                   // lottieView.visibility = View.GONE
//                    cardView.visibility = View.VISIBLE
//
//                    nameTextView.text = name
//                    phoneNumberTextView.text = phoneNumber
//                    addressTextView.text = address
//                    displayMessage("Address added successfully!")
//                } else {
//                    displayMessage("Fields must not be left blank!")
//                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                displayMessage("Operation cancelled!")
                dialog.dismiss()
            }.setNeutralButton("Cancel") { dialog, _ ->
                displayMessage("Operation cancelled!")
                dialog.dismiss()
            }
            .show()
    }

    private fun launchCustomAlertDialogCats() {
        var productinputbinding: CategoriesInputDialogBinding =
            CategoriesInputDialogBinding.inflate(layoutInflater)

//        nameTextField = customAlertDialogView.findViewById(R.id.name_text_field)
//        phoneNumberTextField = customAlertDialogView.findViewById(R.id.phone_number_text_field)
//        addressTextField = customAlertDialogView.findViewById(R.id.address_text_field)
        customAlertDialogView = productinputbinding.root
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle("Add Category")

            .setPositiveButton("Add") { dialog, _ ->

                var category =
                    Category(0, productinputbinding.txtCatName.editText?.text.toString(), null)
                PostCategoryRequest(category)
//                val name = nameTextField.editText?.text.toString()
//                val phoneNumber = phoneNumberTextField.editText?.text.toString()
//                val address = addressTextField.editText?.text.toString()
//
//                val isInputValid : Boolean = validateInput(name, phoneNumber, address)
//
//                if (isInputValid) {
//                   // lottieView.visibility = View.GONE
//                    cardView.visibility = View.VISIBLE
//
//                    nameTextView.text = name
//                    phoneNumberTextView.text = phoneNumber
//                    addressTextView.text = address
//                    displayMessage("Address added successfully!")
//                } else {
//                    displayMessage("Fields must not be left blank!")
//                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                displayMessage("Operation cancelled!")
                dialog.dismiss()
            }.setNeutralButton("Cancel") { dialog, _ ->
                displayMessage("Operation cancelled!")
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Method to validate user input (Basic validation)
     */
    private fun validateInput(name: String, phoneNumber: String, address: String): Boolean {
        if (name.trim().isNotEmpty() && phoneNumber.trim().isNotEmpty()
            && address.trim().isNotEmpty()
        ) {
            return true
        }
        return false
    }

    /**
     * Method to display any message in the form of Toast (can be replaced with Snack bar)
     */
    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadBitmap(multipartBody: ByteArray) {

        //getting the tag from the edittext
        //   val tags: String = editTextTags.getText().toString().trim()

        //our custom volley request
        val xx: VolleyMultipartRequestKot = VolleyMultipartRequestKot(
            Request.Method.POST, Config.BASE_URL + "Products/UploadFile",
            { response ->
                val resultResponse = String(response.data)
                try {
                    Log.e("Messsage", resultResponse)
                    Toast.makeText(
                        applicationContext,
                        resultResponse.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
//                    val result = JSONObject(resultResponse)
//                    val status = result.getString("status")
//                    val message = result.getString("message")
////                    if (status == Constant.REQUEST_SUCCESS) {
//                        // tell everybody you have succed upload image and post strings
//                        Log.e("Messsage", status+message)
//                    } else {
//                        Log.i("Unexpected", message)
//                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { error ->
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            })


        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, Config.BASE_URL + "Products/UploadFile",
            Response.Listener { response ->
                val resultResponse = String(response.data)
                try {
                    Log.e("Messsage", resultResponse)
                    Toast.makeText(
                        applicationContext,
                        resultResponse.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
//                    val result = JSONObject(resultResponse)
//                    val status = result.getString("status")
//                    val message = result.getString("message")
////                    if (status == Constant.REQUEST_SUCCESS) {
//                        // tell everybody you have succed upload image and post strings
//                        Log.e("Messsage", status+message)
//                    } else {
//                        Log.i("Unexpected", message)
//                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["tags"] = ""
                return params
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                val imagename = System.currentTimeMillis()
                params["file"] = DataPart("$imagename.png", multipartBody)
                return params
            }

            override fun getHeaders(): Map<String, String>? {
                val headers: HashMap<String, String> = HashMap()
                //  headers["Authorization"] = "Basic $base64EncodedCredentials"
                headers["Authorization"] = "Bearer " + appSettings?.getToken()
                return headers
            }
        }

        //adding the request to volley
        //Volley.newRequestQueue(this).add(volleyMultipartRequest)
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(volleyMultipartRequest)
    }


    fun PostCategoryRequest(product: Category) {

        // var userid=appSettings.g
//        var carx: CartDetails[] = [{ cD_Pr_id: productid, cD_id:0, cartForeignKey: 0, cD_Pr_Amnt: 0, cD_Pr_price: 0, cD_Pr_Qty: 0, cart: null, product: null, productForeignKey:0 }];
//        var cart = {
//            cart_id: 0, status: "PENDING", totalAmount: 0, totalQty: 0, userID: heroes, cartDetails: carx
//        } as Cart;

        var extract = appSettings?.getToken()?.split(".")?.get(1)
        val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
        //Toast.makeText(context,decodedBytes.toString(), Toast.LENGTH_LONG).show()

        val decodedString = String(decodedBytes)
        val javaRootMapObject: Map<*, *> = Gson().fromJson(
            decodedString,
            Map::class.java
        )
        val jsonString = Gson().toJson(product)
        val jsonObject = JSONObject(jsonString)

//        val paramsx = HashMap<String,String>()
//        paramsx["username"] = "tayyab"
//        paramsx["password"] = "123456"
        //  val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "Categories",
            jsonObject,
            Response.Listener { response ->


//                var token=  response.get("token").toString()
//                var extract= token.split(".")[1]
//                val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
//                val decodedString = String(decodedBytes)
//                val javaRootMapObject: Map<*, *> = Gson().fromJson(
//                    decodedString,
//                    Map::class.java
//                )
                Toast.makeText(applicationContext,response.toString(), Toast.LENGTH_LONG).show()
//                activityViewModel.getProducts(getUser())
//                appSettings?.saveLoggedIn(true)
//                appSettings?.saveToken(token)
//                appSettings?.saveIsAdmin(javaRootMapObject.get("UserID")!!.equals("ADMIN"))
//                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
//                //intent.putExtra("WID", obj.WID)
//                startActivity(intent)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext,error.message.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {

                if (response != null) {
                    if(response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        //intent.putExtra("WID", obj.WID)
                        startActivity(intent)
                        finish()
                    }
                }
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if(volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                        appSettings?.saveLoggedIn(false);
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        //intent.putExtra("WID", obj.WID)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings?.getToken()
                return params
            }
        }

        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }
}
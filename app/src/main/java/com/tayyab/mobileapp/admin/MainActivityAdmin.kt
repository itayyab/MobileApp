package com.tayyab.mobileapp.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.AuthActivity
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.databinding.ActivityMainAdminBinding
import com.tayyab.mobileapp.databinding.CategoriesInputDialogBinding
import com.tayyab.mobileapp.databinding.ProductsInputDialogBinding
import com.tayyab.mobileapp.interfaces.SelectImageListener
import com.tayyab.mobileapp.interfaces.VolleyCallback
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.util.*
import kotlin.collections.ArrayList


class MainActivityAdmin : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var customAlertDialogView: View
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var viewModel: MainActivityAdminViewModel
    private lateinit var appSettings: AppSettings
    var categoryList: ArrayList<Category>? = null
    var selectImageListener: SelectImageListener? = null

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
        viewModel.getDialogStart().observe(this, { t ->
            if (t.product) {
                launchCustomAlertDialogProducts(true, t.data)
            } else {
                launchCustomAlertDialogCats(true, t.data)
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
            val id = navController.currentDestination!!.id
            if (id == R.id.nav_categories) {
                launchCustomAlertDialogCats(false, null)
            } else if (id == R.id.nav_products) {
                launchCustomAlertDialogProducts(false, null)
            }
        }
        getCats(object : VolleyCallback {
            override fun onSuccess(result: String) {
                TODO("Not yet implemented")
            }

            override fun onSuccessArrayList(result: java.util.ArrayList<Category>) {
                categoryList = result
            }
        })
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

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val mImageUri: Uri? = intent!!.data

                selectImageListener?.onSuccess(mImageUri.toString())
//                                val product = Product(null,productinputbinding.selectCategory.editText.text)
            }
        }

    private fun selectpic() {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        startForResult.launch(photoPickerIntent)

    }

    private fun addProductWithImage(uri: Uri, product: Product) {
        val bmp = getBitmapFromUri(uri)
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        uploadBitmap(imageBytes, object : VolleyCallback {
            override fun onSuccess(result: String) {
                product.pr_Picture = result
                postProductRequest(product)
            }

            override fun onSuccessArrayList(result: ArrayList<Category>) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun updateProductWithImage(uri: Uri, product: Product) {
        val bmp = getBitmapFromUri(uri)
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        uploadBitmap(imageBytes, object : VolleyCallback {
            override fun onSuccess(result: String) {
                product.pr_Picture = result
                postProductRequest(product)
            }

            override fun onSuccessArrayList(result: ArrayList<Category>) {
                TODO("Not yet implemented")
            }
        })

    }
    

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun launchCustomAlertDialogProducts(edit: Boolean, product: Any?) {
        val productinputbinding =
            ProductsInputDialogBinding.inflate(layoutInflater)
        customAlertDialogView = productinputbinding.root
        productinputbinding.btnImageSelect.setOnClickListener { view ->
            selectImageListener = object : SelectImageListener {
                override fun onSuccess(result: String) {
                    displayMessage(result)
                    productinputbinding.txtSelectedImage.text = result
                }
            }
            selectpic()
        }
        if (edit) {
            var actval = product as Product
            productinputbinding.txtProductName.editText?.setText(actval.pr_name)
            productinputbinding.txtProductDesc.editText?.setText(actval.pr_desc)
            productinputbinding.txtProductPrice.editText?.setText(actval.pr_price.toString())
            productinputbinding.txtSelectedImage.text = actval.pr_Picture
            materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setView(customAlertDialogView)
                .setTitle("Edit Product")
                .setPositiveButton("Update") { dialog, _ ->
                    val catid = categoryList?.stream()
                        ?.filter { s -> s.cat_name == productinputbinding.selectCategory.editText?.text.toString() }
                        ?.findAny()
                        ?.orElse(null)
                    val product = Product(
                        0,
                        productinputbinding.txtProductName.editText?.text.toString(),
                        productinputbinding.txtProductPrice.editText?.text.toString().toInt(),
                        productinputbinding.txtProductDesc.editText?.text.toString(),
                        "",
                        null,
                        catid!!.cat_id
                    )
                    if (productinputbinding.txtSelectedImage.text.toString() != "" && productinputbinding.txtSelectedImage.text.toString() == "No Image") {
                        updateProductWithImage(
                            Uri.parse(productinputbinding.txtSelectedImage.text.toString()),
                            product
                        )
                    } else {
                        product.pr_Picture = "StaticFiles/Images/no_image.png"
                        updateProductRequest(product)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Delete") { dialog, _ ->
                    deleteProductRequest(actval)
                    dialog.dismiss()
                }.setNeutralButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            val cats = categoryList?.map { it.cat_name }
            val adapter = ArrayAdapter(applicationContext, R.layout.list_item, cats!!)
            (productinputbinding.selectCategory.editText as? AutoCompleteTextView)?.setAdapter(
                adapter
            )
            materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setView(customAlertDialogView)
                .setTitle("Add Product")
                .setPositiveButton("Add") { dialog, _ ->
                    val catid = categoryList?.stream()
                        ?.filter { s -> s.cat_name == productinputbinding.selectCategory.editText?.text.toString() }
                        ?.findAny()
                        ?.orElse(null)
                    val product = Product(
                        0,
                        productinputbinding.txtProductName.editText?.text.toString(),
                        productinputbinding.txtProductPrice.editText?.text.toString().toInt(),
                        productinputbinding.txtProductDesc.editText?.text.toString(),
                        "",
                        null,
                        catid!!.cat_id
                    )
                    if (productinputbinding.txtSelectedImage.text.toString() != "" && productinputbinding.txtSelectedImage.text.toString() == "No Image") {
                        addProductWithImage(
                            Uri.parse(productinputbinding.txtSelectedImage.text.toString()),
                            product
                        )
                    } else {
                        product.pr_Picture = "StaticFiles/Images/no_image.png"
                        postProductRequest(product)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    displayMessage("Operation cancelled!")
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun launchCustomAlertDialogCats(edit: Boolean, category: Any?) {
        var productinputbinding: CategoriesInputDialogBinding =
            CategoriesInputDialogBinding.inflate(layoutInflater)
        customAlertDialogView = productinputbinding.root
        if (edit) {
            var actval = category as Category
            productinputbinding.txtCatName.editText?.setText(actval.cat_name)
            materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setView(customAlertDialogView)
                .setTitle("Edit Category")
                .setPositiveButton("Update") { dialog, _ ->
                    val categoryx =
                        Category(
                            actval.cat_id,
                            productinputbinding.txtCatName.editText?.text.toString(),
                            null
                        )
                    updateCategoryRequest(categoryx)
                    dialog.dismiss()
                }
                .setNegativeButton("Delete") { dialog, _ ->
                    deleteCategoryRequest(actval)
                    dialog.dismiss()
                }.setNeutralButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setView(customAlertDialogView)
                .setTitle("Add Category")
                .setPositiveButton("Add") { dialog, _ ->
                    val category =
                        Category(0, productinputbinding.txtCatName.editText?.text.toString(), null)
                    postCategoryRequest(category)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    displayMessage("Operation cancelled!")
                    dialog.dismiss()
                }
                .show()
        }
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

    private fun uploadBitmap(multipartBody: ByteArray, volleyCallback: VolleyCallback) {
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, Config.BASE_URL + "Products/UploadFile",
            Response.Listener { response ->
                val resultResponse = String(response.data)
                try {
                    Log.e("Messsage", resultResponse)
                    volleyCallback.onSuccess(resultResponse)
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

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
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

            override fun getHeaders(): Map<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer " + appSettings.getToken()
                return headers
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(volleyMultipartRequest)
    }

    private fun postProductRequest(product: Product) {
        val jsonString = Gson().toJson(product)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "Products",
            jsonObject,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(applicationContext, "Product Added Successfully", Toast.LENGTH_LONG)
                    .show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false)
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    fun updateProductRequest(product: Product) {
        val jsonString = Gson().toJson(product)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.PUT,
            Config.BASE_URL + "Products/" + product.pr_id,
            jsonObject,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(
                    applicationContext,
                    "Product Updated Successfully",
                    Toast.LENGTH_LONG
                ).show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(
                    applicationContext,
                    "Eroor:" + error.message.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                try {
                    if (response!!.data.size === 0) {
                        val responseData = "{}".toByteArray(charset("UTF8"))

                        var responsex = NetworkResponse(
                            response!!.statusCode,
                            responseData,
                            response.notModified,
                            response.networkTimeMs,
                            response.allHeaders
                        )
                        return parseNetworkResponse(responsex)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false);
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    fun deleteProductRequest(product: Product) {
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE,
            Config.BASE_URL + "Products/" + product.pr_id,
            null,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(
                    applicationContext,
                    "Product Deleted Successfully",
                    Toast.LENGTH_LONG
                ).show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false)
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    private fun postCategoryRequest(category: Category) {
        val jsonString = Gson().toJson(category)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "Categories",
            jsonObject,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(applicationContext, "Category Added Successfully", Toast.LENGTH_LONG)
                    .show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false)
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    fun updateCategoryRequest(category: Category) {
        val jsonString = Gson().toJson(category)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.PUT,
            Config.BASE_URL + "Categories/" + category.cat_id,
            jsonObject,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(
                    applicationContext,
                    "Category Updated Successfully",
                    Toast.LENGTH_LONG
                ).show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(
                    applicationContext,
                    "Eroor:" + error.message.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                try {
                    if (response!!.data.size === 0) {
                        val responseData = "{}".toByteArray(charset("UTF8"))

                        var responsex = NetworkResponse(
                            response!!.statusCode,
                            responseData,
                            response.notModified,
                            response.networkTimeMs,
                            response.allHeaders
                        )
                        return parseNetworkResponse(responsex)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false);
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    fun deleteCategoryRequest(category: Category) {
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE,
            Config.BASE_URL + "Categories/" + category.cat_id,
            null,
            Response.Listener { response ->
                viewModel.getDataUpdated()
                Toast.makeText(
                    applicationContext,
                    "Category Deleted Successfully",
                    Toast.LENGTH_LONG
                ).show()
            },
            Response.ErrorListener { error ->// Do something when error occurred
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        appSettings.saveLoggedIn(false)
                        val intent = Intent(applicationContext, AuthActivity::class.java)
                        startActivity(intent)
                        //finish()
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonArrayRequest)
    }

    private fun getCats(volleyCallback: VolleyCallback) {
        var jsonreq = ApiUtils.GsonRequestForCat<ArrayList<Category>>(
            Config.BASE_URL + "Categories",
            ArrayList(),
            null,
            { ServerResponse ->
                volleyCallback.onSuccessArrayList(ServerResponse)
            },
            { volleyError ->

                Log.e("RESP:", volleyError.toString())
            });
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(jsonreq)
    }
}
package com.tayyab.mobileapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityShopViewModel
import com.tayyab.mobileapp.databinding.FragmentHomeBinding
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.adapters.ProductsAdapter
import com.tayyab.mobileapp.interfaces.OnItemClickListener
import com.tayyab.mobileapp.models.CartDetail
import com.tayyab.mobileapp.models.Carts
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.AutoClearedValue
import com.tayyab.mobileapp.utils.VolleySingleton
import org.json.JSONObject
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var bindingx: AutoClearedValue<FragmentHomeBinding>? = null
    private var adapter: AutoClearedValue<ProductsAdapter>? = null
    private var wordsadapter: ProductsAdapter? = null
    lateinit var editText: EditText
    lateinit var startString: String
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var appSettings: AppSettings?=null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val activityViewModel: MainActivityShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        bindingx = AutoClearedValue(this@HomeFragment, _binding!!)
        val root: View = binding.root
        appSettings= AppSettings(requireContext())

        bottomSheetBehavior = BottomSheetBehavior.from<FrameLayout>(binding.standardBottomSheet)
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //   textView.text = it
//        })
//
//        homeViewModel!!.text2.observe(viewLifecycleOwner, Observer {
//            //Log.e("RESP:",it)
//        })

        homeViewModel.getProductsStart().observe(viewLifecycleOwner,
            Observer<List<Product>> { t ->
                bindingx!!.get().progressBar.visibility = View.VISIBLE
                adapter!!.get().insertData(t!!)
                bindingx!!.get().progressBar.visibility = View.GONE
            })



        val recyclerWords: RecyclerView = bindingx!!.get().words
        val mLayoutManagerwords = LinearLayoutManager(context)
        recyclerWords.layoutManager = mLayoutManagerwords
        wordsadapter = ProductsAdapter(R.layout.product_list_item, recyclerWords)
        adapter = AutoClearedValue(this, wordsadapter!!)
        recyclerWords.adapter = adapter!!.get()
        adapter!!.get().setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(obj: Product) {

                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED||bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                  //  persistentBtn.text = "Close Bottom Sheet"
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                   // persistentBtn.text = "Show Bottom Sheet"
                }
                binding.imageView?.loadUrl(Config.IMAGE_BASE_URL + obj.pr_Picture)
                binding.product = obj
                val obj = object : OnSpeakClickListener{
                    override fun onSpeakClick(obj: Product, shift: Boolean) {
                        sendRequest(obj)
                    }
                }
                binding.speakclicklistner = obj
//                val intent = Intent(this@DicRecyclerFragment.context, ActivityDetails::class.java)
//                intent.putExtra("WID", obj.WID)
//                intent.putExtra("Word", obj.Word)
//                intent.putExtra("Meaning", obj.Meaning)
//                intent.putExtra("is_user_added", obj.is_user_added)
//                intent.putExtra("synonym", obj.synonym)
//                intent.putExtra("antonym", obj.antonym)
//                intent.putExtra("Type", obj.Type)
//                intent.putExtra("shift", shift)
//                startActivity(intent)
            }
        })
        adapter!!.get().setOnSpeakClickListener(object : OnSpeakClickListener{
            override fun onSpeakClick(obj: Product, shift: Boolean) {
                sendRequest(obj)

            }
        })

        val textInputCustomEndIcon: TextInputLayout = bindingx!!.get().textinputlayout

        editText = _binding!!.edittext
        //  editText = TextInputEditText(fragmentDicRecyclerBinding!!.textinputlayout.getContext());
        //   editText = TextInputEditText(binding!!.get().textinputlayout.context)
        textInputCustomEndIcon.isEndIconVisible = false;
        textInputCustomEndIcon.setEndIconOnClickListener {
            if (editText.text!!.isNotEmpty()) {
                editText.text = null
            }
        }



        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // val txt: String = editText.getText().toString()
                // or String txt = s.toString();
                // or String txt = s.toString();
                // if (!txt.isEmpty()) {

                //}

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                textInputCustomEndIcon.isEndIconVisible = count > 0
                adapter!!.get().filter.filter(s.toString())
                //  Toast.makeText(context,s.toString(),Toast.LENGTH_SHORT).show()
            }
        })


        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // adapter!!.get().filter.filter(editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })




        //sendRequest()
        return root

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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(context,"RESUME",Toast.LENGTH_SHORT).show()

        bindingx!!.get().progressBar.visibility = View.VISIBLE
        homeViewModel!!.getProducts()
    }


    fun sendRequest(product:Product) {

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
        Toast.makeText(context,decodedString.toString(), Toast.LENGTH_LONG).show()
        var carxd: List<CartDetail> = listOf(CartDetail(0,1,product.pr_id,0,0,null,0,null,0))
        var cart = Carts(carxd,0,"PENDING",0,0,javaRootMapObject.get("UserID").toString())

        val jsonString = Gson().toJson(cart)
        val jsonObject = JSONObject(jsonString)

//        val paramsx = HashMap<String,String>()
//        paramsx["username"] = "tayyab"
//        paramsx["password"] = "123456"
      //  val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL+"Carts",
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
                Toast.makeText(context,response.toString(), Toast.LENGTH_LONG).show()
                activityViewModel.getProducts(getUser())
//                appSettings?.saveLoggedIn(true)
//                appSettings?.saveToken(token)
//                appSettings?.saveIsAdmin(javaRootMapObject.get("UserID")!!.equals("ADMIN"))
//                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
//                //intent.putExtra("WID", obj.WID)
//                startActivity(intent)
            },
            Response.ErrorListener {  error ->// Do something when error occurred
                Toast.makeText(context,error.message.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer "+appSettings?.getToken()
                return params
            }
        }

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrayRequest)
    }
    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
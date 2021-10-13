package com.tayyab.mobileapp.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.adapters.ProductsAdapter
import com.tayyab.mobileapp.adapters.ProductsCrudAdapter
import com.tayyab.mobileapp.databinding.FragmentProductsBinding
import com.tayyab.mobileapp.databinding.FragmentShopBinding
import com.tayyab.mobileapp.interfaces.OnProductItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.models.CartDetail
import com.tayyab.mobileapp.models.Carts
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.ui.shop.ShopViewModel
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.AutoClearedValue
import com.tayyab.mobileapp.utils.VolleySingleton
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private lateinit var shopViewModel: ShopViewModel
    private var bindingx: AutoClearedValue<FragmentProductsBinding>? = null
    private var adapter: AutoClearedValue<ProductsCrudAdapter>? = null
    private var wordsadapter: ProductsCrudAdapter? = null
    lateinit var editText: EditText
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var appSettings: AppSettings?=null
    private val activityViewModel: MainActivityAdminViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        shopViewModel =
            ViewModelProvider(this).get(ShopViewModel::class.java)
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        bindingx = AutoClearedValue(this@ProductsFragment, _binding!!)
        val root: View = binding.root
        appSettings= AppSettings(requireContext())
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        shopViewModel.getProductsStart().observe(viewLifecycleOwner,
            { t ->
                bindingx!!.get().progressBar.visibility = View.VISIBLE
                adapter!!.get().insertData(t!!)
                bindingx!!.get().progressBar.visibility = View.GONE
            })

        activityViewModel.dataupdated?.observe(viewLifecycleOwner, Observer { list ->
            // Update the list UI
            Log.e("DBG:", "dataupdated observed")
            shopViewModel.getProducts()
        })

        val recyclerWords: RecyclerView = bindingx!!.get().words
        val mLayoutManagerwords = LinearLayoutManager(context)
        recyclerWords.layoutManager = mLayoutManagerwords
        wordsadapter = ProductsCrudAdapter(R.layout.product_crud_list_item, recyclerWords)
        adapter = AutoClearedValue(this, wordsadapter!!)
        recyclerWords.adapter = adapter!!.get()
        adapter!!.get().setOnItemClickListener(object : OnProductItemClickListener {
            override fun onItemClick(obj: Product) {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED||bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    //  persistentBtn.text = "Close Bottom Sheet"
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    // persistentBtn.text = "Show Bottom Sheet"
                }
                binding.imageView.loadUrl(Config.IMAGE_BASE_URL + obj.pr_Picture)
                binding.product = obj
                val objx = object : OnProductItemClickListener{
                    override fun onItemClick(obj: Product) {
                        activityViewModel.getDialogs(true,obj)
                    }
                }
                binding.productclicklistner=objx
            }
        })
        val textInputCustomEndIcon: TextInputLayout = bindingx!!.get().textinputlayout

        editText = _binding!!.edittext
        textInputCustomEndIcon.isEndIconVisible = false;
        textInputCustomEndIcon.setEndIconOnClickListener {
            if (editText.text!!.isNotEmpty()) {
                editText.text = null
            }
        }



        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

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

//                 adapter!!.get().filter.filter(editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        bindingx!!.get().progressBar.visibility = View.VISIBLE
        shopViewModel.getProducts()
    }
    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
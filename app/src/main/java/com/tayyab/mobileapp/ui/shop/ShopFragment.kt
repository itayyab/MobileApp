package com.tayyab.mobileapp.ui.shop

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityShopViewModel
import com.tayyab.mobileapp.adapters.ProductsAdapter
import com.tayyab.mobileapp.databinding.FragmentShopBinding
import com.tayyab.mobileapp.dialogs.LoginBottomSheetDialog
import com.tayyab.mobileapp.interfaces.AuthCallback
import com.tayyab.mobileapp.interfaces.OnProductItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.interfaces.VolleyCRUDCallback
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.*
import org.json.JSONObject


class ShopFragment : Fragment() {

    private lateinit var shopViewModel: ShopViewModel
    private var _binding: FragmentShopBinding? = null
    private var bindingx: AutoClearedValue<FragmentShopBinding>? = null
    private var adapter: AutoClearedValue<ProductsAdapter>? = null
    private var wordsadapter: ProductsAdapter? = null
    lateinit var editText: EditText
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var appSettings: AppSettings? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val activityViewModel: MainActivityShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        shopViewModel =
            ViewModelProvider(this).get(ShopViewModel::class.java)

        _binding = FragmentShopBinding.inflate(inflater, container, false)

        bindingx = AutoClearedValue(this@ShopFragment, _binding!!)
        val root: View = binding.root
        appSettings = AppSettings(requireContext())

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        shopViewModel.getProductsStart().observe(viewLifecycleOwner,
            { t ->
                bindingx!!.get().progressBar.visibility = View.VISIBLE
                adapter!!.get().insertData(t!!)
                bindingx!!.get().progressBar.visibility = View.GONE
            })


        val recyclerWords: RecyclerView = bindingx!!.get().words


        val mLayoutManagerwords = LinearLayoutManager(context)
        val staggeredGridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerWords.layoutManager=staggeredGridLayoutManager
//        recyclerWords.layoutManager = mLayoutManagerwords



        wordsadapter = ProductsAdapter(R.layout.product_list_item, recyclerWords)
        adapter = AutoClearedValue(this, wordsadapter!!)
        recyclerWords.adapter = adapter!!.get()
        adapter!!.get().setOnItemClickListener(object : OnProductItemClickListener {
            override fun onItemClick(obj: Product) {

                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    //  persistentBtn.text = "Close Bottom Sheet"
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    // persistentBtn.text = "Show Bottom Sheet"
                }
                binding.imageView.loadUrl(Config.IMAGE_BASE_URL + obj.pr_Picture)
                binding.product = obj
                val objx = object : OnSpeakClickListener {
                    override fun onSpeakClick(obj: Product, shift: Boolean) {
                        addToCart(obj)
                    }
                }
                binding.speakclicklistner = objx
            }
        })
        adapter!!.get().setOnSpeakClickListener(object : OnSpeakClickListener {
            override fun onSpeakClick(obj: Product, shift: Boolean) {
                addToCart(obj)

            }
        })

        val textInputCustomEndIcon: TextInputLayout = bindingx!!.get().textinputlayout

        editText = _binding!!.edittext
        //  editText = TextInputEditText(fragmentDicRecyclerBinding!!.textinputlayout.getContext());
        //   editText = TextInputEditText(binding!!.get().textinputlayout.context)
        textInputCustomEndIcon.isEndIconVisible = false
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
            }
        })


        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // adapter!!.get().filter.filter(editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        return root

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

    fun addToCart(product: Product) {
        binding.viewDisableLayout.visibility = View.VISIBLE
        val shopApis = ShopApis(requireContext())
        shopApis.addToCartRequest(product, object : VolleyCRUDCallback {
            override fun onSuccess(result: JSONObject) {
                Toast.makeText(context, "Product added to cart successfully!", Toast.LENGTH_LONG)
                    .show()
                val authUtils = AuthUtils(requireContext())
                activityViewModel.getProducts(authUtils.getUserID())
                shopViewModel.getProducts()
                binding.viewDisableLayout.visibility = View.GONE
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(
                    requireContext(),
                    "Error:" + error.message.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
                binding.viewDisableLayout.visibility = View.GONE
            }

            override fun onAuthFailed(statusCode: Int) {
                appSettings?.saveLoggedIn(false)
                login()
            }
        })
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    fun login(){
        val bottomSheet = LoginBottomSheetDialog(object : AuthCallback {
            override fun onSuccess(result: JSONObject) {
                binding.viewDisableLayout.visibility = View.GONE
            }

            override fun onFailed(error: VolleyError) {
                binding.viewDisableLayout.visibility = View.GONE
            }
        })
        bottomSheet.show(childFragmentManager, "")
    }
}
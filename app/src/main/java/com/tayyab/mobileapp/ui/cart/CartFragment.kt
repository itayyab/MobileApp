package com.tayyab.mobileapp.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityShopViewModel
import com.tayyab.mobileapp.adapters.CartAdapter
import com.tayyab.mobileapp.databinding.FragmentCartBinding
import com.tayyab.mobileapp.dialogs.LoginBottomSheetDialog
import com.tayyab.mobileapp.interfaces.AuthCallback
import com.tayyab.mobileapp.interfaces.OnCartItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.interfaces.VolleyCRUDCallback
import com.tayyab.mobileapp.models.CartDetail
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.AuthUtils
import com.tayyab.mobileapp.utils.AutoClearedValue
import com.tayyab.mobileapp.utils.ShopApis
import org.json.JSONObject

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var bindingx: AutoClearedValue<FragmentCartBinding>? = null
    private var adapter: AutoClearedValue<CartAdapter>? = null
    private var wordsadapter: CartAdapter? = null
    var appSettings: AppSettings? = null
    private val activityViewModel: MainActivityShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cartViewModel =
            ViewModelProvider(this).get(CartViewModel::class.java)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        appSettings = AppSettings(requireContext())
        val root: View = binding.root

        bindingx = AutoClearedValue(this@CartFragment, _binding!!)

        cartViewModel.getCartsStart().observe(viewLifecycleOwner,
            { t ->
                Log.e("TEST", t.toString())
                bindingx!!.get().progressBar.visibility = View.VISIBLE
                binding.cart = t.firstOrNull()
                if (t.isNotEmpty() && t.firstOrNull() != null && t.firstOrNull()?.cartDetails!!.isNotEmpty()) {
                    adapter!!.get().insertData(t.firstOrNull()?.cartDetails!!)
                    binding.btnCheckout.isEnabled = true
                    binding.mainContainer.visibility = View.VISIBLE
                    binding.lytNoItem.root.visibility = View.GONE
                } else {
                    binding.btnCheckout.isEnabled = false
                    binding.mainContainer.visibility = View.GONE
                    binding.lytNoItem.root.visibility = View.VISIBLE
                }
                bindingx!!.get().progressBar.visibility = View.GONE
            })

        val recyclerWords: RecyclerView = bindingx!!.get().words
        val mLayoutManagerwords = LinearLayoutManager(context)
        recyclerWords.layoutManager = mLayoutManagerwords
        wordsadapter = CartAdapter(R.layout.cartdetails_list_item, recyclerWords)
        adapter = AutoClearedValue(this, wordsadapter!!)
        recyclerWords.adapter = adapter!!.get()
        adapter!!.get().setOnItemClickListener(object : OnCartItemClickListener {
            override fun onItemClick(obj: CartDetail) {
                deleteFromCart(obj.product!!)
            }
        })
        adapter!!.get().setOnSpeakClickListener(object : OnSpeakClickListener {
            override fun onSpeakClick(obj: Product, shift: Boolean) {
            }
        })
        binding.btnCheckout.setOnClickListener {
            checkoutCart()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(context,"RESUME",Toast.LENGTH_SHORT).show()

        bindingx!!.get().progressBar.visibility = View.VISIBLE
        val authUtils = AuthUtils(requireContext())
        cartViewModel.getCarts(authUtils.getUserID())
    }

    fun deleteFromCart(product: Product) {
        binding.viewDisableLayout.visibility = View.VISIBLE
        val shopApis = ShopApis(requireContext())
        shopApis.deleteProductFromCartRequest(product.pr_id, object : VolleyCRUDCallback {
            override fun onSuccess(result: JSONObject) {
                Toast.makeText(
                    context,
                    "Product deleted from cart successfully!",
                    Toast.LENGTH_LONG
                ).show()
                val authUtils = AuthUtils(requireContext())
                activityViewModel.getProducts(authUtils.getUserID())
                cartViewModel.getCarts(authUtils.getUserID())
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

    private fun checkoutCart() {
        binding.viewDisableLayout.visibility = View.VISIBLE
        val shopApis = ShopApis(requireContext())
        shopApis.checkoutCartRequest(object : VolleyCRUDCallback {
            override fun onSuccess(result: JSONObject) {
                Toast.makeText(context, "Checkout completed successfully!", Toast.LENGTH_LONG)
                    .show()
                val authUtils = AuthUtils(requireContext())
                activityViewModel.getProducts(authUtils.getUserID())
                cartViewModel.getCarts(authUtils.getUserID())
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
package com.tayyab.mobileapp.dialogs

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.databinding.LayoutCategoryinputBottomSheetBinding
import com.tayyab.mobileapp.interfaces.SelectImageListener
import com.tayyab.mobileapp.interfaces.VolleyCRUDCallback
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.ShopApis
import org.json.JSONObject


class CategoryBottomSheetDialog : BottomSheetDialogFragment() {
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    var editProduct: Boolean = false
    private lateinit var viewModel: MainActivityAdminViewModel
    private lateinit var appSettings: AppSettings
    var category: Category? = null

    lateinit var productinputbinding: LayoutCategoryinputBottomSheetBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (editProduct) {
            productinputbinding.btnUpdate.visibility = View.VISIBLE
            productinputbinding.btnDelete.visibility = View.VISIBLE
            productinputbinding.btnSave.visibility = View.GONE
            productinputbinding.txtTitle.text="Edit Category"
            productinputbinding.nameToolbar.text="Edit Category"
        } else {
            productinputbinding.btnUpdate.visibility = View.GONE
            productinputbinding.btnDelete.visibility = View.GONE
            productinputbinding.btnSave.visibility = View.VISIBLE
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        productinputbinding =
            LayoutCategoryinputBottomSheetBinding.inflate(layoutInflater)

        //setting layout with bottom sheet
        bottomSheet.setContentView(productinputbinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(
            productinputbinding.root.parent
                    as View
        )
        appSettings = AppSettings(requireContext())
        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior!!.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        productinputbinding.extraSpace.minimumHeight =
            Resources.getSystem().displayMetrics.heightPixels

        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
//                    bottomSheetBehavior!!.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
//                    bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);
                    showView(productinputbinding.appBarLayout, actionBarSize)
                    hideAppBar(productinputbinding.mainContainer)
                    productinputbinding.txtTitle.visibility = View.GONE
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    hideAppBar(productinputbinding.appBarLayout)
                    showView(productinputbinding.mainContainer, actionBarSize)
                    productinputbinding.txtTitle.visibility = View.VISIBLE
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })


        if (editProduct) {
            productinputbinding.txtCategoryName.editText?.setText(category?.cat_name)
        }


        productinputbinding.btnCancel.setOnClickListener { dismiss() }

        productinputbinding.btnDelete.setOnClickListener {
            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            val shopApis = ShopApis(requireContext())
            shopApis.deleteCategoryRequest(category!!,object :VolleyCRUDCallback{
                override fun onSuccess(result: JSONObject) {
                    viewModel.getDataUpdated()
                    Toast.makeText(
                        requireContext(),
                        "Category Deleted Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    clearText()
                    dismiss()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }

                override fun onAuthFailed(statusCode: Int) {
                    appSettings.saveLoggedIn(false)
                    login()
                    enabled(true)
                }
            })

        }
        productinputbinding.btnSave.setOnClickListener {
            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            if (checkAllFields()) {
                val category =
                    Category(
                        0,
                        productinputbinding.txtCategoryName.editText?.text.toString(),
                        null
                    )
                val shopApis = ShopApis(requireContext())
                shopApis.postCategoryRequest(category,object : VolleyCRUDCallback{
                    override fun onSuccess(result: JSONObject) {
                        viewModel.getDataUpdated()
                        Toast.makeText(requireContext(), "Category Added Successfully", Toast.LENGTH_LONG)
                            .show()
                        clearText()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onAuthFailed(statusCode: Int) {
                        appSettings.saveLoggedIn(false)
                        login()
                        enabled(true)
                    }
                })

            } else {
                enabled(true)
            }
        }
        productinputbinding.btnUpdate.setOnClickListener {
            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            if (checkAllFields()) {
                val shopApis = ShopApis(requireContext())
                val categoryx =
                    Category(
                        category!!.cat_id,
                        productinputbinding.txtCategoryName.editText?.text.toString(),
                        null
                    )
                shopApis.updateCategoryRequest(categoryx,object : VolleyCRUDCallback{
                    override fun onSuccess(result: JSONObject) {
                        viewModel.getDataUpdated()
                        Toast.makeText(
                            requireContext(),
                            "Category Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        clearText()
                        dismiss()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(
                            requireContext(),
                            "Eroor:" + error.message.toString(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                    override fun onAuthFailed(statusCode: Int) {
                        appSettings.saveLoggedIn(false)
                        login()
                        enabled(true)
                    }
                })
            } else {
                enabled(true)
            }
        }

        //hiding app bar at the start
        hideAppBar(productinputbinding.appBarLayout)
        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun hideAppBar(view: View) {
        val params = view.layoutParams
        params.height = 0
        view.layoutParams = params
    }

    private fun showView(view: View, size: Int) {
        val params = view.layoutParams
        params.height = size
        view.layoutParams = params
    }

    fun setViewModel(viewModel: MainActivityAdminViewModel) {
        this.viewModel = viewModel
    }


    private val actionBarSize: Int
        get() {
            val array =
                requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
            return array.getDimension(0, 0f).toInt()
        }


    private fun checkAllFields(): Boolean {
        if (productinputbinding.txtCategoryName.editText?.text.toString().isEmpty()) {
            productinputbinding.txtCategoryName.error = "Category name is required"
            return false
        }


        productinputbinding.txtCategoryName.isErrorEnabled = false


        return true
    }

    fun clearText() {
        productinputbinding.txtCategoryName.editText?.text = null
        enabled(true)
    }

    fun enabled(boolean: Boolean) {
        productinputbinding.txtCategoryName.isEnabled = boolean
        productinputbinding.btnDelete.isEnabled = boolean
        productinputbinding.btnUpdate.isEnabled = boolean
        productinputbinding.btnSave.isEnabled = boolean
        productinputbinding.btnCancel.isEnabled = boolean
        if (boolean)
            productinputbinding.progressBar.visibility = View.GONE

    }

    fun login(){
        val bottomSheet = LoginBottomSheetDialog()
        bottomSheet.show(childFragmentManager, "")
    }

}
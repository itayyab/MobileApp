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
import com.tayyab.mobileapp.interfaces.AuthCallback
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

    private lateinit var productinputbinding: LayoutCategoryinputBottomSheetBinding
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
            productinputbinding.txtTitle.text = "Edit Category"
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
            enabled(false)
            val shopApis = ShopApis(requireContext())
            shopApis.deleteCategoryRequest(category!!, object : VolleyCRUDCallback {
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
                }
            })

        }
        productinputbinding.btnSave.setOnClickListener {
            enabled(false)
            if (checkAllFields()) {
                val category =
                    Category(
                        0,
                        productinputbinding.txtCategoryName.editText?.text.toString(),
                        null
                    )
                val shopApis = ShopApis(requireContext())
                shopApis.postCategoryRequest(category, object : VolleyCRUDCallback {
                    override fun onSuccess(result: JSONObject) {
                        viewModel.getDataUpdated()
                        Toast.makeText(
                            requireContext(),
                            "Category Added Successfully",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        clearText()
                        dismiss()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(
                            requireContext(),
                            error.message.toString(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                    override fun onAuthFailed(statusCode: Int) {
                        appSettings.saveLoggedIn(false)
                        login()
                    }
                })

            } else {
                enabled(true)
            }
        }
        productinputbinding.btnUpdate.setOnClickListener {
            enabled(false)
            if (checkAllFields()) {
                val shopApis = ShopApis(requireContext())
                val categoryx =
                    Category(
                        category!!.cat_id,
                        productinputbinding.txtCategoryName.editText?.text.toString(),
                        null
                    )
                shopApis.updateCategoryRequest(categoryx, object : VolleyCRUDCallback {
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
                    }
                })
            } else {
                enabled(true)
            }
        }


        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun setViewModel(viewModel: MainActivityAdminViewModel) {
        this.viewModel = viewModel
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
        if (boolean) {
            productinputbinding.viewDisableLayout.visibility = View.GONE
        } else {
            productinputbinding.viewDisableLayout.visibility = View.VISIBLE
        }

    }

    fun login() {
        val bottomSheet = LoginBottomSheetDialog(object : AuthCallback {
            override fun onSuccess(result: JSONObject) {
                enabled(true)
            }

            override fun onFailed(error: VolleyError) {
                enabled(true)
            }
        })
        bottomSheet.show(childFragmentManager, "")
    }

}
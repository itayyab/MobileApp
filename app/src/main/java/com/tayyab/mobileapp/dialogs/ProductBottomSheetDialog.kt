package com.tayyab.mobileapp.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.databinding.LayoutProductinputBottomSheetBinding
import com.tayyab.mobileapp.interfaces.*
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.ShopApis
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProductBottomSheetDialog : BottomSheetDialogFragment() {
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    var editProduct: Boolean = false
    private var selectImageListener: SelectImageListener? = null
    private lateinit var viewModel: MainActivityAdminViewModel
    private lateinit var appSettings: AppSettings
    var categoryList: ArrayList<Category>? = null
    var product: Product? = null
    lateinit var productinputbinding: LayoutProductinputBottomSheetBinding
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
            productinputbinding.txtTitle.text = "Edit Product"
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
            LayoutProductinputBottomSheetBinding.inflate(layoutInflater)

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
            Resources.getSystem().displayMetrics.heightPixels / 2

        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })


//        productinputbinding.progressBar.visibility = View.VISIBLE
        enabled(false)
        val shopApis = ShopApis(requireContext())
        shopApis.getCats(object : VolleyCallback {
            override fun onError(error: VolleyError) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(result: String) {
                TODO("Not yet implemented")
            }

            override fun onSuccessArrayList(result: java.util.ArrayList<Category>) {
                categoryList = result
                val cats = categoryList?.map { it.cat_name }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, cats!!)
                (productinputbinding.selectCategory.editText as? AutoCompleteTextView)?.setAdapter(
                    adapter
                )
//                productinputbinding.progressBar.visibility = View.VISIBLE
                enabled(true)
                if (editProduct) {
                    productinputbinding.txtProductName.editText?.setText(product?.pr_name)
                    productinputbinding.txtProductDesc.editText?.setText(product?.pr_desc)
                    productinputbinding.txtProductPrice.editText?.setText(product?.pr_price.toString())
                    productinputbinding.txtSelectedImage.editText?.setText(product?.pr_Picture)
                    val catid = categoryList?.stream()
                        ?.filter { s -> s.cat_id == product?.category?.cat_id }?.findAny()
                        ?.orElse(null)
                    val cattext =
                        productinputbinding.selectCategory.editText as? AutoCompleteTextView
                    cattext?.setText(catid?.cat_name.toString(), false)
                }
            }
        })

        productinputbinding.txtSelectedImage.isEndIconVisible = true
        productinputbinding.txtSelectedImage.setEndIconOnClickListener {
            if (productinputbinding.txtSelectedImage.editText?.text.toString() == "No Image") {
                Toast.makeText(requireContext(), "No Image", Toast.LENGTH_LONG).show()
                selectImageListener = object : SelectImageListener {
                    override fun onSuccess(result: String) {
                        productinputbinding.txtSelectedImage.editText!!.setText(result)
                    }
                }
                selectpic()
            } else {
                productinputbinding.txtSelectedImage.editText!!.setText("No Image")
            }

        }

        productinputbinding.btnCancel.setOnClickListener { dismiss() }

        productinputbinding.btnDelete.setOnClickListener {
//            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            val shopApis = ShopApis(requireContext())
            shopApis.deleteProductRequest(product!!, object : VolleyCRUDCallback {
                override fun onSuccess(result: JSONObject) {
                    viewModel.getDataUpdated()
                    Toast.makeText(
                        requireContext(),
                        "Product Deleted Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    clearText()
                    dismiss()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    enabled(true)
                }

                override fun onAuthFailed(statusCode: Int) {
                    appSettings.saveLoggedIn(false)
                    login()
                }
            })
        }
        productinputbinding.btnSave.setOnClickListener {
//            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            if (checkAllFields()) {
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

                if (productinputbinding.txtSelectedImage.editText?.text.toString() != "" && productinputbinding.txtSelectedImage.editText?.text.toString() != "No Image") {
                    addProductWithImage(
                        Uri.parse(productinputbinding.txtSelectedImage.editText?.text.toString()),
                        product
                    )
                } else {
                    product.pr_Picture = "StaticFiles/Images/no_image.png"
                    addProduct(product)
                }
            } else {
                enabled(true)
            }
        }
        productinputbinding.btnUpdate.setOnClickListener {
//            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            if (checkAllFields()) {
                val catid = categoryList?.stream()
                    ?.filter { s -> s.cat_name == productinputbinding.selectCategory.editText?.text.toString() }
                    ?.findAny()
                    ?.orElse(null)
                val product = Product(
                    product!!.pr_id,
                    productinputbinding.txtProductName.editText?.text.toString(),
                    productinputbinding.txtProductPrice.editText?.text.toString().toInt(),
                    productinputbinding.txtProductDesc.editText?.text.toString(),
                    "",
                    null,
                    catid!!.cat_id
                )
                if (productinputbinding.txtSelectedImage.editText?.text.toString() != "" && productinputbinding.txtSelectedImage.editText?.text.toString() != "No Image") {
                    updateProductWithImage(
                        Uri.parse(productinputbinding.txtSelectedImage.editText?.text.toString()),
                        product
                    )
                } else {
                    product.pr_Picture = "StaticFiles/Images/no_image.png"
                    updateProduct(product)
                }
            } else {
                enabled(true)
            }
        }
        productinputbinding.txtSelectedImage.editText!!.addTextChangedListener(object :
            TextWatcher {
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
                if (s.toString() == "No Image") {
                    productinputbinding.txtSelectedImage.endIconDrawable =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_openfile, null)
                } else {
                    productinputbinding.txtSelectedImage.endIconDrawable =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_close, null)
                }
            }
        })

        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val mImageUri: Uri? = intent!!.data
                selectImageListener?.onSuccess(mImageUri.toString())
            }
        }

    private fun addProductWithImage(uri: Uri, product: Product) {
        val bmp = getBitmapFromUri(uri)
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val shopApis = ShopApis(requireContext())
        shopApis.uploadBitmap(imageBytes, object : VolleyImageUploadCallback {
            override fun onSuccess(result: String) {
                product.pr_Picture = result
                addProduct(product)
            }

            override fun onAuthFailed(statusCode: Int) {
                appSettings.saveLoggedIn(false)
                login()
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(
                    requireContext(),
                    error.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
//                enabled(true)
            }
        })

    }

    private fun updateProductWithImage(uri: Uri, product: Product) {
        val bmp = getBitmapFromUri(uri)
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val shopApis = ShopApis(requireContext())
        shopApis.uploadBitmap(imageBytes, object : VolleyImageUploadCallback {
            override fun onSuccess(result: String) {
                product.pr_Picture = result
                updateProduct(product)
            }

            override fun onAuthFailed(statusCode: Int) {
                appSettings.saveLoggedIn(false)
                login()
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(
                    requireContext(),
                    error.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    fun addProduct(product: Product) {
        val shopApis = ShopApis(requireContext())
        shopApis.postProductRequest(product, object : VolleyCRUDCallback {
            override fun onSuccess(result: JSONObject) {
                viewModel.getDataUpdated()
                Toast.makeText(requireContext(), "Product Added Successfully", Toast.LENGTH_LONG)
                    .show()
                clearText()
                dismiss()
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG)
                    .show()
                enabled(true)
            }

            override fun onAuthFailed(statusCode: Int) {
                appSettings.saveLoggedIn(false)
                login()
            }
        })
    }

    fun updateProduct(product: Product) {
        val shopApis = ShopApis(requireContext())
        shopApis.updateProductRequest(product, object : VolleyCRUDCallback {
            override fun onSuccess(result: JSONObject) {
                viewModel.getDataUpdated()
                Toast.makeText(
                    requireContext(),
                    "Product Updated Successfully",
                    Toast.LENGTH_LONG
                ).show()
                clearText()
                dismiss()
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(
                    requireContext(),
                    "Error:" + error.message.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
                enabled(true)
            }

            override fun onAuthFailed(statusCode: Int) {
                appSettings.saveLoggedIn(false)
                login()
            }
        })
    }


    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun selectpic() {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        startForResult.launch(photoPickerIntent)

    }


    fun setViewModel(viewModel: MainActivityAdminViewModel) {
        this.viewModel = viewModel
    }


    private fun checkAllFields(): Boolean {
        if (productinputbinding.txtProductName.editText?.text.toString().isEmpty()) {
            productinputbinding.txtProductName.error = "Product name is required"
            return false
        }
        if (productinputbinding.txtProductDesc.editText?.text.toString().isEmpty()) {
            productinputbinding.txtProductDesc.error = "Description is required"
            return false
        }
        if (productinputbinding.txtProductPrice.editText?.text.toString().isEmpty()) {
            productinputbinding.txtProductPrice.error = "Price is required"
            return false
        }
        if (productinputbinding.selectCategory.editText?.text.toString().isEmpty()) {
            productinputbinding.selectCategory.error = "Category is required"
            return false
        }

        productinputbinding.txtProductName.isErrorEnabled = false
        productinputbinding.txtProductDesc.isErrorEnabled = false
        productinputbinding.txtProductPrice.isErrorEnabled = false
        productinputbinding.selectCategory.isErrorEnabled = false


        return true
    }

    fun clearText() {
        productinputbinding.txtProductName.editText?.text = null
        productinputbinding.txtProductDesc.editText?.text = null
        productinputbinding.txtProductPrice.editText?.text = null
        productinputbinding.selectCategory.editText?.text = null
        productinputbinding.txtSelectedImage.editText?.setText("No Image")
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
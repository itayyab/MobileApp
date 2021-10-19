package com.tayyab.mobileapp.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.databinding.LayoutLoginBottomSheetBinding
import com.tayyab.mobileapp.interfaces.AuthCallback
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.ShopApis
import org.json.JSONObject


class LoginBottomSheetDialog(var authCallback: AuthCallback) : BottomSheetDialogFragment() {
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private lateinit var appSettings: AppSettings

    private lateinit var productinputbinding: LayoutLoginBottomSheetBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        productinputbinding =
            LayoutLoginBottomSheetBinding.inflate(layoutInflater)

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
               /* if (BottomSheetBehavior.STATE_EXPANDED == i) {
//                    bottomSheetBehavior!!.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
//                    bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    showView(productinputbinding.appBarLayout, actionBarSize)
//                    hideAppBar(productinputbinding.mainContainer)
//                    productinputbinding.txtTitle.visibility = View.GONE
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
//                    hideAppBar(productinputbinding.appBarLayout)
//                    showView(productinputbinding.mainContainer, actionBarSize)
                    productinputbinding.txtTitle.visibility = View.VISIBLE
                }*/
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        productinputbinding.btnLogin.setOnClickListener {
            productinputbinding.progressBar.visibility = View.VISIBLE
            enabled(false)
            val shopApis = ShopApis(requireContext())
            if (checkAllFields()) {
                shopApis.sendLoginRequest(
                    productinputbinding.textUsername.editText?.text.toString(),
                    productinputbinding.textPassword.editText?.text.toString(),
                    object : AuthCallback {
                        override fun onSuccess(result: JSONObject) {
                            val token = result.get("token").toString()
                            val extract = token.split(".")[1]
                            val decodedBytes = Base64.decode(extract, Base64.DEFAULT)
                            val decodedString = String(decodedBytes)
                            val javaRootMapObject: Map<*, *> = Gson().fromJson(
                                decodedString, Map::class.java
                            )
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG)
                                .show()
                            appSettings.saveLoggedIn(true)
                            appSettings.saveToken(token)
                            appSettings.saveIsAdmin(
                                javaRootMapObject.get("role")!! == "ADMIN"
                            )
                            clearText()
                            authCallback.onSuccess(result)
                            dismiss()

                        }

                        override fun onFailed(error: VolleyError) {
                            Toast.makeText(
                                context,
                                "Login failed. Error:" + error.networkResponse.statusCode.toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                            enabled(true)
                        }
                    }
                )
            } else {
                enabled(true)
            }
        }

        return bottomSheet
    }

    override fun onDismiss(dialog: DialogInterface) {
        authCallback.onFailed(VolleyError())
        super.onDismiss(dialog)
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun checkAllFields(): Boolean {
        if (productinputbinding.textUsername.editText?.text.toString().isEmpty()) {
            productinputbinding.textUsername.error = "Username is required"
            return false
        }
        if (productinputbinding.textPassword.editText?.text.toString().isEmpty()) {
            productinputbinding.textPassword.error = "Password is required"
            return false
        }
        productinputbinding.textUsername.isErrorEnabled = false
        productinputbinding.textPassword.isErrorEnabled = false

        return true
    }

    fun clearText() {
        productinputbinding.textUsername.editText?.text = null
        productinputbinding.textPassword.editText?.text = null
        enabled(true)
    }

    fun enabled(boolean: Boolean) {
        productinputbinding.textUsername.isEnabled = boolean
        productinputbinding.textPassword.isEnabled = boolean
        productinputbinding.btnLogin.isEnabled = boolean
        if (boolean)
            productinputbinding.progressBar.visibility = View.GONE

    }

}
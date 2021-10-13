package com.tayyab.mobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.databinding.FragmentLoginBinding
import com.tayyab.mobileapp.utils.VolleySingleton
import org.json.JSONObject
import com.google.gson.Gson
import com.tayyab.mobileapp.admin.MainActivityAdmin
import com.tayyab.mobileapp.interfaces.AuthCallback
import com.tayyab.mobileapp.utils.AppSettings
import com.tayyab.mobileapp.utils.ShopApis
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    lateinit var appSettings: AppSettings

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
        }
        appSettings = AppSettings(requireContext())
        if (appSettings.getLoggedIn()) {
            if (appSettings.getIsAdmin()) {
                val intent = Intent(this@LoginFragment.context, MainActivityAdmin::class.java)
                //intent.putExtra("WID", obj.WID)
                startActivity(intent)
                activity?.finish()
            } else {
                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
                //intent.putExtra("WID", obj.WID)
                startActivity(intent)
                activity?.finish()
            }

        }
        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            enabled(false)
            val shopApis = ShopApis(requireContext())
            if (checkAllFields()) {
                shopApis.sendLoginRequest(
                    binding.textUsername.editText?.text.toString(),
                    binding.textPassword.editText?.text.toString(),
                    object : AuthCallback {
                        override fun onSuccess(result: JSONObject) {
                            val token = result.get("token").toString()
                            val extract = token.split(".")[1]
                            val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
                            val decodedString = String(decodedBytes)
                            val javaRootMapObject: Map<*, *> = Gson().fromJson(
                                decodedString, Map::class.java
                            )
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG)
                                .show()
                            appSettings.saveLoggedIn(true)
                            appSettings.saveToken(token)
                            appSettings.saveIsAdmin(
                                javaRootMapObject.get("role")!!.equals("ADMIN")
                            )
                            clearText()

                            if (appSettings.getIsAdmin()) {
                                val intent = Intent(this@LoginFragment.context, MainActivityAdmin::class.java)
                                //intent.putExtra("WID", obj.WID)
                                startActivity(intent)
                                activity?.finish()
                            } else {
                                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
                                //intent.putExtra("WID", obj.WID)
                                startActivity(intent)
                                activity?.finish()
                            }
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
    }

    private fun checkAllFields(): Boolean {
        if (binding.textUsername.editText?.text.toString().isEmpty()) {
            binding.textUsername.error = "Username is required"
            return false
        }
        if (binding.textPassword.editText?.text.toString().isEmpty()) {
            binding.textPassword.error = "Password is required"
            return false
        }
        binding.textUsername.isErrorEnabled = false
        binding.textPassword.isErrorEnabled = false

        return true
    }

    fun clearText() {
        binding.textUsername.editText?.text = null
        binding.textPassword.editText?.text = null
        enabled(true)
    }

    fun enabled(boolean: Boolean) {
        binding.textUsername.isEnabled = boolean
        binding.textPassword.isEnabled = boolean
        binding.btnLogin.isEnabled = boolean
        binding.btnRegister.isEnabled = boolean
        if (boolean)
            binding.progressBar.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
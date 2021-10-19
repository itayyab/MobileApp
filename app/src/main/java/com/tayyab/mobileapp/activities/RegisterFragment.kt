package com.tayyab.mobileapp.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.VolleyError
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.databinding.FragmentRegisterBinding
import com.tayyab.mobileapp.interfaces.AuthCallback
import org.json.JSONObject
import android.text.TextUtils
import android.util.Patterns
import com.tayyab.mobileapp.utils.ShopApis


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_Second2Fragment_to_First2Fragment)
        }
        binding.btnRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            enabled(false)
            val shopApis = ShopApis(requireContext())
            if (checkAllFields()) {
                shopApis.sendRegisterRequest(
                    binding.textUsername.editText?.text.toString(),
                    binding.textEmail.editText?.text.toString(),
                    binding.textPassword.editText?.text.toString(),
                    binding.textFullName.editText?.text.toString(),
                    object : AuthCallback {
                        override fun onSuccess(result: JSONObject) {
                            binding.progressBar.visibility = View.GONE
                            if (result.getBoolean("succeeded")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.btnLogin.visibility = View.VISIBLE
                                clearText()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration failed:" + result.getJSONArray("errors")
                                        .toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                enabled(true)
                            }
                        }

                        override fun onFailed(error: VolleyError) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Error" + error.message.toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                            enabled(true)
                        }
                    }
                )
            }else{
                enabled(true)
            }
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    private fun checkAllFields(): Boolean {
        if (binding.textUsername.editText?.text.toString().isEmpty()) {
            binding.textUsername.error = "Username is required"
            return false
        }
        if (binding.textEmail.editText?.text.toString()
                .isEmpty() || !isValidEmail(binding.textEmail.editText?.text.toString())
        ) {
            binding.textEmail.error = "Email is required"
            return false
        }

        if (binding.textPassword.editText?.text.toString().isEmpty()) {
            binding.textPassword.error = "Password is required"
            return false
        }
        if (binding.textFullName.editText?.text.toString().isEmpty()) {
            binding.textFullName.error = "Full Name is required"
            return false
        }

        if (binding.textConfirmPassword.editText?.text.toString().isEmpty()) {
            binding.textConfirmPassword.error = "Password is required"
            return false
        }

        if (binding.textPassword.editText?.text.toString() != binding.textConfirmPassword.editText?.text.toString()) {
            binding.textPassword.error = "Passwords don't match"
            binding.textConfirmPassword.error = "Passwords don't match"
            return false
        }

        binding.textUsername.isErrorEnabled = false
        binding.textFullName.isErrorEnabled = false
        binding.textPassword.isErrorEnabled = false
        binding.textEmail.isErrorEnabled = false
        binding.textConfirmPassword.isErrorEnabled = false

        return true
    }

    fun clearText() {
        binding.textUsername.editText?.text = null
        binding.textFullName.editText?.text = null
        binding.textPassword.editText?.text = null
        binding.textEmail.editText?.text = null
        binding.textConfirmPassword.editText?.text = null
        enabled(true)
    }
    fun enabled(boolean: Boolean) {
        binding.textUsername.isEnabled=boolean
        binding.textFullName.isEnabled=boolean
        binding.textPassword.isEnabled=boolean
        binding.textEmail.isEnabled=boolean
        binding.textConfirmPassword.isEnabled=boolean
        binding.btnRegister.isEnabled=boolean
        if(boolean)
            binding.progressBar.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
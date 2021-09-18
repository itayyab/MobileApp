package com.tayyab.mobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.databinding.FragmentLoginBinding
import com.tayyab.mobileapp.utils.VolleySingleton
import org.json.JSONObject
import com.google.gson.Gson
import com.tayyab.mobileapp.utils.AppSettings
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    var appSettings: AppSettings?=null

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

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
        }
        appSettings= AppSettings(requireContext())
        if(appSettings!!.getLoggedIn()){
            val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
            //intent.putExtra("WID", obj.WID)
            startActivity(intent)
            activity?.finish()
        }
        binding.btnLogin.setOnClickListener {
            sendRequest()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun sendRequest() {

        val paramsx = HashMap<String,String>()
        paramsx["username"] = "tayyab"
        paramsx["password"] = "123456"
        val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL+"ApplicationUser/Login",
            jsonObjectx,
            Response.Listener { response ->

                var token=  response.get("token").toString()
                var extract= token.split(".")[1]
                val decodedBytes = Base64.decode(extract,Base64.DEFAULT);
                val decodedString = String(decodedBytes)
                val javaRootMapObject: Map<*, *> = Gson().fromJson(
                    decodedString,
                    Map::class.java
                )
                Toast.makeText(context,decodedString.toString(),Toast.LENGTH_LONG).show()
                appSettings?.saveLoggedIn(true)
                appSettings?.saveToken(token)
                appSettings?.saveIsAdmin(javaRootMapObject.get("role")!!.equals("ADMIN"))
                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
                //intent.putExtra("WID", obj.WID)
                startActivity(intent)
            },
            Response.ErrorListener {  error ->// Do something when error occurred
                Toast.makeText(context,error.message.toString(),Toast.LENGTH_LONG).show()
            }
        ) {
        }

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrayRequest)
    }
}